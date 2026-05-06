from math import ceil
from django.http import HttpResponseRedirect
from django.shortcuts import redirect, render
from app_base.models import Person, Unite
from app_controle.models import Controle, DetailEquipe, DetailUnite, Equipe, EquipeUnite, Mission, MissionUnite, RetrieveList
from app_controle.serializers import UniteSerializer

from rest_framework.viewsets import ModelViewSet
from rest_framework.permissions import IsAuthenticated


from app_controle.utils import getUniteByEquipe, getUniteByMission, getUniteByUser, uniteByUser

from django.core.paginator import Paginator

from app_user.models import Profile, User
from app_user.utils import UnionCmd


class UniteAPIViewset(ModelViewSet):
    serializer_class = UniteSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        id_user = self.request.user.id
        user = User.objects.get(pk=id_user)

        try:
            profile = user.profile.id
        except Profile.DoesNotExist:
            profile = None

        querySet = []
        if profile == 1:
            querySet = getUniteByUser(user)
        if profile == 2:
            querySet = getUniteByEquipe(user)
        if profile == 3:
            querySet = getUniteByMission(user)
        if profile == 4 or user.is_superuser:
            querySet = Unite.objects.all()
        return querySet

def liste_unite(request):
    personnes = Person.objects.all().order_by('name')

    id_user = request.user.id
    user = User.objects.get(pk=id_user)
   
    data =[]

    profile = None

    try:
        profile = user.profile
    except Exception:
        profile = None
    
    if profile != None:
        unites = uniteByUser(id_user)
        for u in unites:
            n = personnes.filter(unit=u.name).count()
            data.append({
                'u' : u,
                'n' : n
            })        
    else:
        unites = Unite.objects.all()
        for u in unites:
            n = personnes.filter(unit=u.name).count()
            data.append({
                'u' : u,
                'n' : n
            })
            
    
    if request.method == "POST":
            rech = request.POST['rech']
            unites = unites.filter(name__icontains=rech)
            data = []
            for u in unites:
                n = personnes.filter(unit=u.name).count()
                data.append({
                    'u' : u,
                    'n' : n
                })

    p = Paginator(data,20)
    page = request.GET.get('page')
    pages_unite =p.get_page(page)

    ids = list()
    for d in data:
        ids.append(d.get('u').id)
    ids = ids[:170]


    ctx = {
        'compte' : len(unites),
        'pages_unite' : pages_unite,
        'lunite' : 'active',
        'data' : ids
    }
    return render(request,'app_controle/unites.html',ctx)

def editunite(request, myid):

    sel_unite = Unite.objects.get(id = myid)
    liste_person = Person.objects.all()

   
    ctx = {
        'sel_unite': sel_unite,
        'liste_person': liste_person,
    }
    return render(request,'app_controle/unites.html',ctx)

def update_unite(request, myid):
    unite  = Unite.objects.get(pk = myid)
    com = request.POST["com"]
    
    #com = Person.objects.get(pk=icom)

    unite.commandant = com
    unite.save()
    
    return HttpResponseRedirect('/controle/unites/')

def update_listcmd(request, id):
    unite = Unite.objects.get(pk=id)
    user = User.objects.get(pk=request.user.id)

    if user.profile.id == 2:
        liste_ctr = Controle.objects.filter(unite=unite.name)
        liste_cmd = RetrieveList.objects.filter(unite=unite.name)
        liste_bdd = Person.objects.filter(unit=unite.name)
        ctr_cmd = liste_ctr.filter(liste_cmd=True)

        ncmd = len(liste_cmd)
        nctr = len(liste_ctr)
        nctr_cmd = len(ctr_cmd)
        nbdd = len(liste_bdd)

        p = 0
        if ncmd > 0 : p = ceil((nctr_cmd * 100) / ncmd)

        ctx = {
            "p": p,
            "ncmd":ncmd,
            "nctr":nctr,
            "nctr_cmd":nctr_cmd,
            "nbdd":nbdd,
            "unite":unite,
            "live": False
        }

        return render(request, 'app_controle/update_liste_unite.html', ctx)
    else :
        return redirect('/')

def updating_liste(request, id):
    user = User.objects.get(pk=request.user.id)
    unite = Unite.objects.get(pk=id)
    
    if user.profile.id == 2:    
        rs_equipe = Equipe.objects.filter(user=user)
        if rs_equipe.exists():
            equipe = rs_equipe.first()
            ce = equipe.user
            cm = equipe.mission.charge_mission
            mission = equipe.mission

            rs_de = DetailEquipe.objects.filter(equipe_id=equipe.id)


            liste_ctr = Controle.objects.filter(unite=unite.name)
            liste_cmd = RetrieveList.objects.filter(unite=unite.name)
            liste_bdd = Person.objects.filter(unit=unite.name)
            ctr_cmd = liste_ctr.filter(liste_cmd=True)

            ncmd = len(liste_cmd)
            nctr = len(liste_ctr)
            nctr_cmd = len(ctr_cmd)
            nbdd = len(liste_bdd)

            p = 0
            if ncmd > 0 : 
                p = ceil((nctr_cmd * 100) / ncmd)
            
            if nctr_cmd == ncmd:
                return redirect('/')

            ctx = {
                "p": p,
                "ncmd":ncmd,
                "nctr":nctr,
                "nctr_cmd":nctr_cmd,
                "nbdd":nbdd,
                "unite":unite,
                "live":True
            }
            if rs_de.exists():
                ctr = rs_de.first()
                UnionCmd(ctr,unite,ce,cm,mission).start()
            return render(request, 'app_controle/updating_liste_unite.html', ctx)
            #return redirect('/controle/update_liste_unite/'+id+'/')


def status_unites(request, id):
    unite = Unite.objects.get(pk=id)
    equipes = Equipe.objects.all()
    missions = Mission.objects.all()
    ctrs = User.objects.filter(profile_id=1)
    ctx = {
        "unite" : unite,
        "equipes" : equipes,
        "missions" : missions,
        "ctrs": ctrs
    }
    id_equipe = -1
    id_mission = -1
    id_ctr = -1

    if request.method == "POST" :
        id_equipe = request.POST['equipe']
        id_mission = request.POST['mission']
        id_ctr = request.POST['ctr']

        if id_equipe != "-1" :
            eu = EquipeUnite(
                equipe_id = id_equipe,
                unite_id = id
            )
            rs_eu = EquipeUnite.objects.filter(equipe_id=id_equipe,unite_id=id)
            if rs_eu.exists():
                pass
            else:
                eu.save()
                
        if id_ctr != "-1" :
            du = DetailUnite(
                user_id = id_ctr,
                unite_id = id
            )
            rs_du = DetailUnite.objects.filter(user_id=id_ctr,unite_id=id)
            if rs_du.exists():
                pass
            else:
                du.save()
        
        
        if id_mission != "-1" :
            mu = MissionUnite(
                mission_id = id_mission,
                unite_id = id
            )
            rs_mu = MissionUnite.objects.filter(mission_id=id_mission,unite_id=id)
            if rs_mu.exists():
                pass
            else:
                mu.save()
        
        return HttpResponseRedirect('/controle/unites/')
    
    return render(request, 'app_controle/status_unites.html', ctx)