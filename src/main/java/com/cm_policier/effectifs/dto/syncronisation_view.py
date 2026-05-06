import base64
import json
from django.http import JsonResponse
from django.shortcuts import redirect, render
import requests

from django.contrib.auth.decorators import login_required
from app_base.models import Person, Unite
from app_base.utils import retrieve_unite
from app_controle.models import Controle, Equipe, Mission, Seance
from app_controle.models.justification import Justification
from app_controle.serializers import ControleSerializer, DocumentSerializer, LoguserSerializer, PersonSerializer, UniteSerializer
from app_controle.utils import confirmIP, getPersonByEquipe, getUniteByEquipe, readIP
from app_user.models import LogUser, User
from django.contrib import messages
from app_user.serializers import UserSerializer
from rest_framework.views import APIView
from rest_framework.permissions import IsAuthenticated
from app_user.utils import Synchronisation, fsynchronization, reinitialisation_controle, statesynchro
from root.utils import generate_qrcode

from root.utils import remove_fields


class SControleView(APIView):

    def post(self, request):
        
        id_user = request.user.id
        data = json.loads(request.body)
        
        id_controle = data.get("id")
        
        p = data.get("present")
        j = data.get("justifie")
        
        dseance = data.get("seance")
        liste_cmd = data.get("liste_cmd")

        person = None
        id_person = data.get("person")
        rperson = Person.objects.filter(currentnrnew=id_person)
        if rperson.exists(): person = rperson.first()

        controleur = None
        id_controleur = data.get("controleur")
        rcontroleur = User.objects.filter(id=id_controleur)
        if rcontroleur.exists: controleur = rcontroleur.first()

        chef_equipe = None
        id_chef_equipe = data.get("chef_equipe")
        rchef_equipe = User.objects.filter(id=id_chef_equipe)
        if rchef_equipe.exists(): chef_equipe = rchef_equipe.first()

        charge_mission = None
        id_charge_mission = data.get("charge_mission")
        rcharge_mission = User.objects.filter(id=id_charge_mission)
        if rcharge_mission.exists():  charge_mission = rcharge_mission.first()

        mission = None
        id_mission = data.get("mission")
        rmission = Mission.objects.filter(id=id_mission)
        if rmission.exists() : mission = rmission.first()

        justification = None
        id_justification = data.get("justification")
        rjustification = Justification.objects.filter(id=id_justification)
        if rjustification.exists(): justification = rjustification.first()

        s = None
        if dseance :
            efseance = ["chef_equipe", "mission"]
            dseance = remove_fields(dseance, efseance)
            s = Seance(**dseance)
            s.chef_equipe = chef_equipe
            s.mission = mission

            if not Seance.objects.filter(id=s.id).exists() : s.save()

        exclude_fields = ['person', "id","controleur", "mission", "seance", "chef_equipe", "charge_mission","documents", "justification"]
        data = remove_fields(data, exclude_fields)

        rc = Controle.objects.filter(id=id_controle)
        if not rc.exists():
            c = Controle(**data)
            c.id = id_controle
            c.person = person
            c.controleur_id = id_controleur
            if s : c.seance_id = s.id
            c.mission_id = id_mission
            c.chef_equipe = chef_equipe
            c.charge_mission = charge_mission
            if mission is None:
                pass
            else:
                c.qrcode = generate_qrcode(c,mission)
            c.save()
        else:
            c = rc.first()
            c.liste_cmd = liste_cmd
            c.save()
        
        c.justification = justification
        c.save()
        
        return JsonResponse(dict(detail=f"ID Controle {id_controle} "), safe=False)

class LoguserView(APIView):

    def post(self, request):
        id_user = request.user.id
        data = json.loads(request.body)

        create_log = data.get("created_at")
        id_user = data.get("user")
        action = data.get("action")

        ruser = User.objects.filter(id=id_user)
        if ruser.exists():
            user = ruser.first()
            rl = LogUser.objects.filter(created_at=create_log, user=user)
        
            if not rl.exists():
                l = LogUser()
                l.user = user
                l.action = action
                l.created_at = create_log
                l.updated_at = create_log
                l.save()

        return JsonResponse(dict(detail=f"Logs"), safe=False)

@login_required
def config(request):
    try:
        data = readIP()
    except FileNotFoundError:
        data = ""

    ctx = {
        "data":data
    }
    return render(request, 'app_user/data.html', ctx)

@login_required
def writeIP(request):
    ip = request.POST['adresse']
    confirmIP(ip)
    ctx = {}
    return redirect('/config/')

@login_required
def import_data(request):

    ip = "http://198.38.84.68:80"
    try:
        ip = readIP()
    except FileNotFoundError:
        pass   

    #Controles
    endpoint = ip + "/api/scontrole/"
    try:
        r = requests.get(endpoint)
        for c in r.json():
            print("C", c.get('noms'))
    except Exception:
        print("Erreur de importation controle :", Exception)


    return redirect('/config/')

@login_required
def init_data(request):
    reinitialisation_controle()
    # persons = Person.objects.all()
    # unites = Unite.objects.all()
    # users = User.objects.all()
    # missions = Mission.objects.all()
    # equipes = Equipe.objects.all()

    # ip = "http://198.38.84.68:80"
    # try:
    #     ip = readIP()
    # except FileNotFoundError:
    #     pass

    # endpoint = ip + "/api/sendusers/"
    # for u in users:
    #     serializer = UserSerializer(u)
    #     try:
    #         r = requests.post(endpoint, json=serializer.data)
    #         print("USER", r.content, "DATA", r.json())
    #     except Exception:
    #         print("Erreur d'initialisation users :", Exception)

    # endpoint = ip + "/api/spersons/"
    # for p in persons:
    #     serializer = PersonSerializer(p)

    #     try:
    #         r = requests.post(endpoint, json=serializer.data)
    #     except Exception:
    #         print("Erreur de synchronisation persons :", Exception)
    
    # endpoint = ip + "/api/smissions/"
    # for m in missions:
    #     serializer = MissionSerializer(m)

    #     try:
    #         r = requests.post(endpoint, json=serializer.data)
    #     except Exception:
    #         print("Erreur de synchronisation missions :", Exception)
    
    # endpoint = ip + "/api/sequipes/"
    # for e in equipes:
    #     serializer = EquipeSerializer(e)

    #     try:
    #         r = requests.post(endpoint, json=serializer.data)
    #     except Exception:
    #         print("Erreur de synchronisation equipes :", Exception)
    
    # endpoint = ip + "/api/sunites/"
    # for u in unites:
    #     serializer = UniteSerializer(u)
    #     try:
    #         r = requests.post(endpoint, json=serializer.data)
    #     except Exception:
    #         print("Erreur de synchronisation unites :", Exception)


    return redirect('/config/')

@login_required
def synchronize_data(request):
    nt = statesynchro()
    ctx = {
        "start" : False,
        "msg" : "DEBUTER LA SYNCHRONISATION",
        "nt": nt 
    } 
    return render(request,'app_controle/syncencours.html',ctx)

@login_required
def  start_synchronization(request, liv):
    nt = statesynchro()

    if liv == 0:
        ctx = {
            "start" : True,
            "msg" : "CONFIRMER LA SYNCHRONISATION",
            "nt": nt
        }
    else:
        ctx = {
            "start" : True,
            "live": True,
            "nt": nt,
            "msg" : "SYNCHRONISATION EN COURS ..."
        }
    if nt == 0:
        return redirect('/')
    else:
        return render(request,'app_controle/syncencours.html',ctx)

def on_synchronization(request):
    
    Synchronisation().start()
    
    return redirect('/start_synchronize/1')

@login_required
def getdata(request):
    id_user = request.user.id
    data = ""

    try:
        user = User.objects.get(pk=id_user)
    except User.DoesNotExist:
        user = None

    if user is None or user.is_active == False :
        data = f"L'utilisateur n'est pas active"
    else:
        if user.profile is None:
            data = f"Le profile est admin"
        else:
            if user.profile.id == 2:
                unites = getUniteByEquipe(user)
                personnes = getPersonByEquipe(user)
                data = f"Profile  {user.profile.name} - unité : {len(unites)} - person : {len(personnes)} "
            elif user.profile.id == 3:
                data = f"Profile  {user.profile.name}"
            elif user.profile.id == 4:
                data = f"Profile  {user.profile.name}"
    ctx = {
        "data":data
    }
    return render(request, 'app_user/data.html', ctx)