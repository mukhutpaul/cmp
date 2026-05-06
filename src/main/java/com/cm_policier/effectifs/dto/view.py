from datetime import datetime
from importlib.metadata import files
import json
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse
from django.shortcuts import redirect, render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer
from rest_framework_simplejwt.views import TokenObtainPairView
from rest_framework.viewsets import ModelViewSet
from app_base.models import Face, Fingerprint, Militaire, Person, Unite
from app_base.utils import retrieve_unite
from app_controle.models import AffectationTablette, DetailEquipe, DetailUnite, Equipe, EquipeUnite, Justification, Mission, MissionUnite, Seance, Tablette
from app_controle.serializers import JustificationSerializer, UniteSerializer
from app_controle.utils import getEquipe, getMission, getUniteByUser
from django.core.paginator import Paginator
from django.contrib import messages
import base64

from rest_framework.permissions import IsAuthenticated

from app_user.utils import Client, create_profile
from django.contrib.auth import login,authenticate,logout

from django.views.generic import View
from django.contrib.auth.decorators import login_required
from rest_framework.parsers import MultiPartParser, FormParser

from app_user.models import LogUser, Profile, User
from app_user.serializers import EquipeSerializer, MissionSerializer, UserSerializer

from django_xhtml2pdf.utils import pdf_decorator

class RegisterViewset(ModelViewSet):
    serializer_class = UserSerializer
    parser_classes = (MultiPartParser, FormParser)
    queryset = User.objects.all()

class ApiTokenSerializer(TokenObtainPairSerializer):
    def validate(self, attrs):
        data = super().validate(attrs)
        refresh = self.get_token(self.user)
        data['refresh'] = str(refresh)
        data['access'] = str(refresh.access_token)
        return data

class ApiToken(TokenObtainPairView):
    serializer_class = ApiTokenSerializer

class MyTokenObtainPairSerializer(TokenObtainPairSerializer):
    def validate(self, attrs):
        data = super().validate(attrs)
        refresh = self.get_token(self.user)
        user = self.user
        data['user'] = user.username

        if user.profile is None or user.profile.id == 4:
            if user.is_superuser:
                data['refresh'] = str(refresh)
                data['access'] = str(refresh.access_token)
                LogUser(user=user,action="Connexion distante du Manager ou Admin").save()
                return data
            else:
                data['refresh'] = str(refresh)
                data['access'] = str(refresh.access_token)
                LogUser(user=user,action="Connexion distante du Manager").save()
                return data
        elif user.profile.id == 2 or user.profile.id == 3 :
            data['refresh'] = str(refresh)
            data['access'] = str(refresh.access_token)
            LogUser(user=user,action="Connexion distante").save()
            return data

        de = DetailEquipe.objects.filter(user_id=self.user.id).first()
        if de is None:
            ndata = {}
            ndata['detail'] = "Vous n'avez pas d'equipe"
            LogUser(user=user,action="Tentative - Pas d'équipe").save()
            return ndata
        else:
            ndata = {}
            ce = de.equipe.user
            s = Seance.objects.filter(chef_equipe_id=ce.id,is_active=True).first()
            if s is None:
                ndata['status'] = "ERREUR"
                ndata['detail'] = "Pas de séance active"
                LogUser(user=user,action="Tentative - Pas de séance active").save()
                return ndata
            else:
                data = super().validate(attrs)
                refresh = self.get_token(user)
                data['refresh'] = str(refresh)
                data['access'] = str(refresh.access_token)
                data['id'] = str(self.user.id)
                data['noms'] = self.user.noms
                data['unites'] = getUniteByUser(self.user)
                data['seance'] = str(s.id)
                ndata['status'] = status.HTTP_200_OK
                justifications = Justification.objects.filter(is_active=True)
                serializer = JustificationSerializer(justifications, many=True)
                data['justifications'] = serializer.data
                ndata['detail'] = data
                LogUser(user=user,action="Connexion distante du controleur").save() 
                return ndata

class MyTokenObtainPairView(TokenObtainPairView):
    serializer_class = MyTokenObtainPairSerializer

@login_required
def listeusers(request):
    listuser= User.objects.order_by('-date_joined')
    profiles = Profile.objects.all()
    ctrep =[]

    if request.method == "POST":
        rech = request.POST['rech']
        p = Paginator(listuser.filter(noms__contains=rech) | listuser.filter(profile__name__contains=rech) , 10)
        page = request.GET.get('page') 
        pages =p.get_page(page)
    else:
        p = Paginator(listuser, 20)
        page = request.GET.get('page') 
        pages =p.get_page(page)       
    
    user = User.objects.get(pk=request.user.id)
    LogUser(user=user,action="Affichage de la liste des utilisateurs")

    ctx = {
        'users' : listuser,
        'pages' : pages,
        'profiles':profiles,
        'compte' : len(listuser),
        "lutilisateur" : 'active'
    }

    return render(request,'app_user/users.html',ctx)

@pdf_decorator(pdfname="Utilisateurs.pdf")
def print_users(request):
    users = User.objects.filter(is_active=True)
    profiles = Profile.objects.all()

    user = User.objects.get(pk=request.user.id)
    LogUser(user=user,action="Impression de la liste des utilisateurs")

    controleurs = []
    ce = []
    cm = []
    managers = []
    data_profiles = []
    
    for user in users:
        if user.profile.id == 1 :
            controleurs.append(user)
        if user.profile.id == 2 :
            ce.append(user)
        if user.profile.id == 3 :
            cm.append(user)
        if user.profile.id == 4 :
            managers.append(user)
    
    data_profiles.append(controleurs)
    data_profiles.append(ce)
    data_profiles.append(cm)
    data_profiles.append(managers)

    ctx = {
        "controleurs" : controleurs,
        "ces" : ce,
        "cms" : cm,
        "managers" : managers,
        "date" : datetime.today
    }    
    return render(request, "app_report/print_users.html", ctx)

@login_required
def logusers(request):
    logusers= LogUser.objects.order_by('-id')
   
    if request.method == "POST":
        rech = request.POST['rech']

        p = Paginator(logusers.filter(user__noms__contains=rech) , 10)
        page = request.GET.get('page') 
        pages =p.get_page(page)

    else:
        p = Paginator(logusers, 10)
        page = request.GET.get('page') 
        pages =p.get_page(page)

    ctx = {
        'compte' : len(logusers),
        'pages' : pages,
        "lloguser" : 'active'
    }

    return render(request,'app_user/logusers.html',ctx)

class getUnite(APIView):

    def post(self,request):
        data = json.loads(request.body)
        name = data.get("name")
        rs_unite = Unite.objects.filter(name=name)
        if rs_unite.exists():
            serializer = UniteSerializer(rs_unite.first())
            return Response(data=serializer.data)
        else:
            return Response(status=status.HTTP_404_NOT_FOUND)
   
@login_required
def adduser(request,):

    if request.method == 'POST':
        photo = request.FILES.get("image_url")
        password  = request.POST.get("password")
        username = request.POST.get("username")
        noms = request.POST.get("noms")
        id_profile = request.POST.get("profile")
        profile = Profile.objects.get(pk=id_profile)

        rs_username = User.objects.filter(username=username)
        try:
            if len(rs_username)>0:
                messages.warning(request,"Ce nom utilisateur existe déjà")
                return redirect('/user/users') 
            else:
                user = User(
                    #email = email,
                    username = username.upper(),
                    noms = noms.upper(),
                    profile = profile,
                    photo_url = photo,
                    is_active = True
                )   
            user.set_password(password)
            user.save()
        except:
            pass

        owner = User.objects.get(pk=request.user.id)
        LogUser(user=owner,action=f"Ajout d'un utilisateur {user.noms} ")
        return HttpResponseRedirect('/user/users/')

    return render(request,'app_user/ajouter_user.html',{'profiles':profile})

def edituser(request, myid):

    sel_user = User.objects.get(id = myid)
    liste_user = User.objects.all()

    p = Paginator(User.objects.all(), 5)
    page = request.GET.get('page')
    pages =p.get_page(page)
    profiles = Profile.objects.all()

    ctx = {
        'sel_user': sel_user,
        'users': liste_user,
        'pages' : pages,
        'nombre' : len(liste_user),
        'profiles': profiles
    }
    return render(request,'app_user/users.html',ctx)

def update_user(request, myid):
    user  = User.objects.get(id = myid)
    id_profile = request.POST.get("profile")
    #email = request.POST.get("email")
    username = request.POST.get("username")
    noms = request.POST.get("noms")
    profile = Profile.objects.get(pk=id_profile)
    
    user.profile = profile
    #user.email = email
    user.usename = username
    user.noms = noms
    user.save()
    
    return HttpResponseRedirect('/user/users/')

def update_user_motdepasse(request):
    user  = User.objects.get(id = request.user.id)
    passew = request.POST.get("pass")
    cpass = request.POST.get("cpass")

    if   passew == cpass: 
        user.set_password(passew) 
        user.save()
       
    else:
        messages.warning(request,"mot de passe incompatible")
        return redirect('/user/page_modifpassword')
        

        
    return HttpResponseRedirect('/user/login/')

def generer_user_motdepasse(request,myid): 
    user  = User.objects.get(id = myid)
    user.set_password("rdc@2022") 
    user.save()

    return HttpResponseRedirect('/user/')

def page_modifpassword(request):
    
    return render(request,'app_user/modifierMotPasse.html')
 
def active_user(request, myid):
       user  = User.objects.get(id = myid)   
       if user.is_active == False:    
          user.is_active = True
          user.save()
       else :
          user.is_active = False
          user.save()

       return HttpResponseRedirect('/user/users/')

class LoginPageView(View):
    template_name = 'app_user/login.html'
    #form_class = forms.LoginForm

    def get(self, request):
        message = ''
        return render(request, self.template_name, context={'message': message})
        
    def post(self, request):
        username = request.POST.get('username')
        password = request.POST.get('password')
        user = authenticate(
            username=username,
            password=password,
        )
        if user is not None:
            login(request, user)
            return redirect('/')
        message = 'Identifiants invalides.'
        return render(request, self.template_name, context={'message': message})

def logout_user(request):
    logout(request)
    return redirect('login')

def edit_profile_user(request, myid):

    sel_user= User.objects.get(id = myid)
    liste_user = User.objects.all()

    p = Paginator(User.objects.all(), 10)
    page = request.GET.get('page')
    pages =p.get_page(page)
    profiles = Profile.objects.all()

    ctx = {
        'sel_user_profile': sel_user,
        'users': liste_user,
        'pages' : pages,
        'nombre' : len(liste_user),
        'profiles': profiles
    }
    return render(request,'app_user/users.html',ctx)

def update_profile_user(request, myid):
    user  = User.objects.get(id = myid)
    id_profile = request.POST.get("profile")
    profile = Profile.objects.get(pk=id_profile)
    user.profile = profile
    user.save()
    
    return HttpResponseRedirect('/user/users/')

def rechercher_user(request):

    if request.method == "POST":
        rech = request.POST['rech']

        users = User.objects.filter(noms__contains=rech)
        

        return render(request,'app_user/users.html',{'rech':rech,
        'users_rech' : users})

    else:
        return render(request,'app_user/users.html')

class UserAPIViewset(ModelViewSet):
    serializer_class = UserSerializer
    permission_classes = [IsAuthenticated]
    parser_classes = (MultiPartParser, FormParser)

    def get_queryset(self):
        id_user = self.request.user.id
        user = User.objects.get(pk=id_user)

        try:
            profile = user.profile.id
        except Profile.DoesNotExist:
            profile = None
        
        querySet = None
        if profile == 2 or profile == 3 or user.is_superuser:    
            querySet = User.objects.filter(id=user.id)

        return querySet

class EquipeAPIViewset(ModelViewSet):
    serializer_class = EquipeSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        id_user = self.request.user.id
        user = User.objects.get(pk=id_user)

        try:
            profile = user.profile.id
        except Profile.DoesNotExist:
            profile = None
        
        querySet = []

        if profile == 2 or profile == 3:    
            querySet = getEquipe(user)

        return querySet

class MissionAPIViewset(ModelViewSet):
    serializer_class = MissionSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        id_user = self.request.user.id
        user = User.objects.get(pk=id_user)

        try:
            profile = user.profile.id
        except Profile.DoesNotExist:
            profile = None
        
        querySet = []

        if profile == 2 or profile == 3:    
            querySet = getMission(user)

        return querySet

def login_remote(request):
    create_profile()

    if request.method == 'POST':
        
        ip = request.POST.get("ip")
        password = request.POST.get("password")
        username = request.POST.get("username")

        client = Client()
        client.go(ip,username,password)

        user_data = client.get_user()
        
        if type(user_data) == list and len(user_data)>0:
            userd = user_data[0]

            user = User(
                id = userd.get('id'),
                email = userd.get('email'),
                username = userd.get('username'),
                noms = userd.get('noms'),
                profile_id = userd.get('profile'),
                finger_print = userd.get('finger_print'),
                photo_url = userd.get('photo_url'),
                is_active = True
            )   
            user.set_password(password)
            if not User.objects.filter(username=username).exists():
                user.save()

            user = authenticate(
                username=username,
                password=password,
            )

            if user is not None:
                # Mission et chargé de mission
                missions = client.get_missions()
                if type(missions)==list and len(missions) > 0:
                    for mission in missions:
                        cm = mission['cm']

                        user_cm = User(
                            id = cm.get('id'),
                            email = cm.get('email'),
                            username = cm.get('username'),
                            profile_id = cm.get('profile_id'),
                            is_active = cm.get('is_active'),
                            password = cm.get('password'),
                            noms = cm.get('noms')
                        )
                        if not User.objects.filter(username=cm.get('username')).exists():
                            user_cm.save()

                        m = Mission(
                                id = mission.get('id'),
                                date_debut = mission.get('date_debut'),
                                date_fin = mission.get('date_fin'),
                                charge_mission_id = cm.get('id'),
                                zone = mission.get('zone'),
                                numero = mission.get('numero')
                            )
                        if not Mission.objects.filter(id=m.id).exists():
                            m.save()
                # Militaires
                dmilitaires = client.get_militaires()
                militaires = []
                if dmilitaires != None : militaires = dmilitaires.get('detail')
                if type(militaires)==list and len(militaires) > 0:
                    for militaire in militaires:
                        if not Militaire.objects.filter(uuid=militaire.get('uuid')).exists():
                            mi = Militaire()
                            mi.uuid = militaire.get('uuid')
                            mi.sex = militaire.get('sex')
                            mi.name = militaire.get('name')
                            mi.firstname = militaire.get('firstname')
                            mi.postname = militaire.get('postname')
                            mi.province = militaire.get('province')
                            mi.unit = militaire.get('unit')
                            mi.currentnrnew = militaire.get('currentnrnew')
                            mi.status = militaire.get('status')
                            mi.grade = militaire.get('grade')
                            mi.birthdate = militaire.get('birthdate')
                            mi.birthplace = militaire.get('birthplace')
                            mi.bloodtype = militaire.get('bloodtype')
                            if Militaire.objects.filter(uuid=mi.uuid).exists():
                                pass
                            else:
                                mi.save()
                

                # Persons
                dpersons = client.get_persons()
                persons = []
                if dpersons != None : persons = dpersons.get('detail')
                if type(persons)==list and len(persons) > 0:
                    for person in persons:
                        if not Person.objects.filter(uuid=person.get('uuid')).exists():
                            p = Person()
                            p.uuid = person.get('uuid')
                            p.sex = person.get('sex')
                            p.name = person.get('name')
                            p.firstname = person.get('firstname')
                            p.postname = person.get('postname')
                            p.province = person.get('province')
                            p.unit = person.get('unit')
                            p.currentnrnew = person.get('currentnrnew')
                            p.status = person.get('status')
                            p.grade = person.get('grade')
                            p.birthdate = person.get('birthdate')
                            p.birthplace = person.get('birthplace')
                            p.bloodtype = person.get('bloodtype')
                            p.save()

                            if person.get('finger') is None:
                                pass
                            else:
                                fds = person.get('finger')
                                for fd in fds:
                                    f = Fingerprint()
                                    f.id = fd.get('id')
                                    f.finger_id = fd.get('finger_id')
                                    f.image = base64.b64decode(fd.get('image')) 
                                    f.template = base64.b64decode(fd.get('template'))
                                    f.uuid = p
                                    f.save()
                            
                            if person.get('face') is None:
                                pass
                            else:
                                fds = person.get('face')
                                for fd in fds: 
                                    f = Face()
                                    f.id = fd['id']
                                    f.data = base64.b64decode(fd['data'])
                                    f.uuid = p
                                    f.save()
                
                #Equipes
                equipes = client.get_equipes()
                if type(equipes)==list and len(equipes) > 0:
                    for equipe in equipes:
                        if not Equipe.objects.filter(id=equipe['id']).exists():
                            mission = Mission.objects.get(pk=equipe['mission'])
                            e = Equipe()
                            e.id = equipe['id']
                            e.user = user
                            e.mission = mission
                            e.save()
                        
                        # Controleurs
                        controleurs = equipe.get('controleur')
                        
                        if type(controleurs)==list and len(controleurs) > 0:
                            for c in controleurs:
                                d_user = c.get('user')
                                
                                rs_user_c = User.objects.filter(id=d_user.get('id'),username=d_user.get('username'))
                                if not rs_user_c.exists():
                                    user_c = User()
                                    user_c.id=c.get('user').get('id')
                                    user_c.email=c.get('user').get('email')
                                    user_c.noms=c.get('user').get('noms')
                                    user_c.username=d_user.get('username')
                                    user_c.finger_print=c.get('user').get('finger_print')
                                    user_c.profile_id=c.get('user').get('profile')
                                    user_c.password=c.get('user').get('password')
                                    user_c.save()
                                else:
                                    user_c = rs_user_c.first()
                                
                                id_de = c.get('id')
                                if not DetailEquipe.objects.filter(user=user_c).exists():
                                    de = DetailEquipe(user=user_c,equipe_id=equipe['id'],created_at=datetime.now())
                                    de.save()
                
                #Unités
                unites = client.get_unites()
                if type(unites)==list and len(unites) > 0:
                    for u in unites:
                        rs_unite = Unite.objects.filter(id=u.get('id'),name=u.get('name'))
                        
                        if rs_unite.exists():
                            obj_u = rs_unite.first()
                        else:
                            obj_u = Unite(
                                id=u.get('id'),
                                name=u.get('name'),
                                #commandant=u.get('commandant'),
                                signature=u.get('signature')
                            )
                            obj_u.save()
                        
                        missionunite = MissionUnite()
                        missionunite.mission = m
                        missionunite.unite = obj_u
                        missionunite.save()

                        #controleur
                        controleurs = u.get('controleur')
                        if type(controleurs)==list and len(controleurs) > 0:
                            for c in controleurs:
                                d_user = c.get('user')
                                rs_user_c = User.objects.filter(id=d_user.get('id'),username=d_user.get('username'))
                                if rs_user_c.exists():
                                    user_c = rs_user_c.first()
                                else:
                                    user_c = User()
                                    user_c.id=d_user.get('id')
                                    user_c.email=d_user.get('email')
                                    user_c.noms=d_user.get('noms')
                                    user_c.username=d_user.get('username'),
                                    user_c.finger_print=d_user.get('finger_print'),
                                    user_c.profile_id=d_user.get('profile')
                                    user_c.password=d_user.get('password')
                                    user_c.save()

                                id_du = c.get('id_du')
                                if not DetailUnite.objects.filter(user=user_c,unite_id=u['id']).exists():
                                    de = DetailUnite(id=id_du,user=user_c,unite_id=u['id'],created_at=datetime.now())
                                    de.save()
                                
                                #Tablette
                                d_aff_tab = c.get('user').get('tablette')
                                  
                                if type(d_aff_tab)==list and len(d_aff_tab) > 0 :
                                    id_aff = d_aff_tab.get('id_aff')
                                    d_tab = d_aff_tab.get('tablette')
                                    
                                    rs_tab = Tablette.objects.filter(serial_number=d_tab.get('serial_number'))
                                    if not rs_tab.exists():
                                        tab = Tablette(id=d_tab.get('id'),serial_number=d_tab.get('serial_number'))
                                        tab.save()
                                    else:
                                        tab = rs_tab.first()
                                    if not AffectationTablette.objects.filter(user=user_c,tablette=tab).exists():
                                        AffectationTablette(id=id_aff,user=user_c,tablette=tab).save()

                        #equipes
                        equipes = u.get('equipes')
                        if type(equipes)==list and len(equipes) > 0:
                            for e in equipes:
                                d_equipe = e.get('equipe')
                                rs_equipe_u = Equipe.objects.filter(user_id=d_equipe.get('user'),mission_id=d_equipe.get('mission'))
                                if not rs_equipe_u.exists():
                                    equipe_u = Equipe(id=d_equipe.get('id'),user_id=d_equipe.get('user'),mission_id=d_equipe.get('mission'))
                                    equipe_u.save()
                                else:
                                    equipe_u = rs_equipe_u.first()
                                
                                id_de = c.get('id_de')
                                if not EquipeUnite.objects.filter(equipe=equipe_u,unite_id=u['id']).exists():
                                    du = EquipeUnite(id=id_de,equipe=equipe_u,unite_id=u['id'])
                                    du.save()
                
                login(request, user)
                LogUser(user=user,action="Première connexion et récupération des données sur pc").save()
                
                return redirect('/')
        
    return HttpResponseRedirect('/')