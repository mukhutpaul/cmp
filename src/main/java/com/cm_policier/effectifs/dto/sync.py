import base64
import binascii
from datetime import datetime
import threading
import requests
import pathlib
import json
from app_base.models import Face, Fingerprint, Militaire, Person, Unite
from app_controle.models import Controle, DetailEquipe, DetailUnite, Equipe, Mission, MissionUnite, RetrieveList
from app_controle.models.document import Document
from app_controle.models.equipe_unite import EquipeUnite
from app_controle.serializers import ControleSerializer, LoguserSerializer
from app_controle.utils import EQUIPES, getPersonByUserUnite, getPersonFromCmdUnite, readIP

from app_user.models import LogUser, Profile, User
from rest_framework import status
from django.contrib.auth import authenticate, login

from root.utils import update_retrievelist

class Client:

    ip:str = None
    username:str = None
    password:str = None
    user:str = None

    access:str = None
    refresh:str = None

    header_type: str = "Bearer"

    cred_path: pathlib.Path = pathlib.Path("creds.json")

    def go(self, ip=None, username=None, password=None):
        if ip == None:
            try:
                data = readIP()
            except FileNotFoundError:
                data = None
            ip = data
        self.ip = ip
        self.username = username
        self.password = password

        if self.cred_path.exists():
            try:
                data = json.loads(self.cred_path.read_text())
            except Exception:
                data = None
            if data is None:
                self.clear_tokens()
                self.perform_auth()
            else:
                self.access = data.get('access')
                self.refresh = data.get('refresh')
                self.user = data.get('user')

                token_verified = self.verify_token()
                if not token_verified:
                    refreshed = self.perform_refresh()

                    if not refreshed:
                        self.clear_tokens()
                        self.perform_auth()
                if self.user != self.username:
                    self.clear_tokens()
                    self.perform_auth()
        else:
            self.perform_auth()
    
    def get_headers(self, header_type=None):
        _type = header_type or self.header_type
        token = self.access

        if not token:
            return {}
        return {'Authorization': f"{_type} {token}"}
    
    def perform_auth(self):
        endpoint = f"{self.ip}/api/token/"
        status_code = 400
        try:
            r = requests.post(endpoint, json={'username':self.username, 'password':self.password})       
            status_code = r.status_code         
        except Exception:
            Exception(f"Connection refused")
        
        if status_code == 200:
            self.write_creds(r.json())   
    
    def write_creds(self, data:dict):
        if self.cred_path is not None:
            self.access = data.get('access')
            self.refresh = data.get('refresh')
            self.user = data.get('user')
            if self.access and self.refresh and self.user:
                self.cred_path.write_text(json.dumps(data))
    
    def verify_token(self):
        data = {
            "token": f"{self.access}"
        }
        endpoint = f"{self.ip}/api/token/verify/"
        status_code = 400
        try:
            r = requests.post(endpoint, json=data)   
            status_code = r.status_code           
        except requests.exceptions.ConnectionError:
            Exception(f"Connection refused")
        
        return status_code == 200
    
    def clear_tokens(self):
        self.access = None
        self.refresh = None
        self.user = None
        if self.cred_path.exists():
            self.cred_path.unlink()
    
    def perform_refresh(self):
        headers = self.get_headers()
        data = {
            "refresh": f"{self.refresh}"
        }
        endpoint = f"{self.ip}/api/token/refresh/"
        status_code = 400
        try: 
            r = requests.post(endpoint, json=data, headers=headers)
            status_code = r.status_code
        except Exception:
            Exception("Connexion refusée")
        if status_code != 200:
            self.clear_tokens()
            return False
        refresh_data = r.json()
        if not 'access' in refresh_data:
            self.clear_tokens()
            return False
        stored_data = {
            'access': refresh_data.get('access'),
            'refresh': self.refresh
        }
        self.write_creds(stored_data)
        return True

    def get_data(self, endpoint=None):
        headers = self.get_headers()
        endpoint = f"{self.ip}/{endpoint}"
        status_code = 400
        try:
            r = requests.get(endpoint, headers=headers)
            status_code = r.status_code
        except Exception:
            Exception("Requete incomplete")
        
        data = None
        if status_code == 200: 
            data = r.json()
        return data
    
    def post_data(self, data, endpoint=None):
        _type = self.header_type
        token = self.access
        headers = {
            "Authorization": f"{_type} {token}",
            "Content-Type":"application/json",
            "Accept":"application/json"
        }
        endpoint = f"{self.ip}/{endpoint}"
        status_code = 400
        try:
            r = requests.post(endpoint,json=data,headers=headers)
            status_code = r.status_code
        except Exception:
            Exception("Requete incomplete")
        
        data = None
        if status_code != 400: 
            data = r.json()
        
        return data
    
    def post_data_form(self, data, endpoint=None):
        _type = self.header_type
        token = self.access
        headers = {
            "Authorization": f"{_type} {token}",
            "Content-Type":"application/json",
            "Accept":"application/json"
        }
        endpoint = f"{self.ip}/{endpoint}"
        
        status_code = 400
        try:
            r = requests.post(endpoint,data=data,headers=headers)
            status_code = r.status_code
        except Exception:
            Exception("Requete incomplete")
        
        data = None
        if status_code != 400: 
            data = r.json()
        
        return data
    
    def get_user(self):
        return self.get_data("api/user/")
    
    def get_unites(self):
        return self.get_data("api/unites/")
    
    def get_persons(self):
        return self.get_data("api/persons/")
    
    def get_datapersons(self,id):
        return self.get_data(f"api/datapersons/{id}")
    
    def get_equipes(self):
        return self.get_data("api/equipes/")
    
    def get_missions(self):
        return self.get_data("api/missions/")
    
    def get_militaires(self):
        return self.get_data("api/militaires/")

def create_profile():
    profiles = ["Controleur","Chef d'équipe", "Chargé de mission","Manager", "Vérificateur"]
    e_profiles = Profile.objects.all()
    for p in profiles:
        t_profile = Profile.objects.filter(name=p).first()
        if t_profile is None:
            profile = Profile()
            profile.name = p
            profile.save()

class Union(threading.Thread):
    def __init__(self, user, unite, ce, cm, signature, mission):
        self.user = user
        self.unite = unite
        self.ce = ce
        self.cm = cm
        self.signature = signature
        self.mission = mission
        threading.Thread.__init__(self)
    
    def run(self):
        liste_bdd = []
        liste_cmd = []

        liste_bdd = getPersonByUserUnite(self.user, self.unite.id)
        liste_cmd = getPersonFromCmdUnite(self.user, self.unite.id)

        n = self.user.id * 1000000000000
        for m in liste_bdd:
            if Controle.objects.filter(matricule=m.currentnrnew).exists():
                c = Controle.objects.filter(matricule=m.currentnrnew).first()
            else:
                c = Controle()
            
            c.id = n + int(m.currentnrnew)
            c.base_donnee = True
            c.person_id = m.uuid
            nom, postnom, prenom  = m.name or "", m.postname or "", m.firstname or "" 
            c.noms = nom + " " + postnom + " " + prenom
            c.matricule = m.currentnrnew
            c.unite = self.unite.name
            c.sexe = m.sex
            c.grade = m.grade

            c.chef_equipe = self.ce
            c.charge_mission = self.cm
            
            try :
                fingerObj4 = Fingerprint.objects.filter(uuid=m, finger_id=4).first()
                fingerObj = Fingerprint.objects.filter(uuid=m, finger_id=7).first()
                
            except Fingerprint.DoesNotExist:
                fingerObj4 = None
                fingerObj = None
            
            try:
                faceObj = Face.objects.get(uuid=m)
            except Face.DoesNotExist:
                faceObj = None

            if fingerObj4 is not None : c.fingerprint4 = binascii.hexlify(fingerObj4.image).decode('utf-8') 
            if fingerObj is not None : c.fingerprint = binascii.hexlify(fingerObj.image).decode('utf-8')
            if faceObj is not None : c.face = binascii.hexlify(faceObj.data).decode('utf-8')
            #hashlib.md5(self.image).hexdigest()

            c.mission = self.mission
            c.save()
        
        for m in liste_cmd:
            if Controle.objects.filter(matricule=m.matricule).exists():
                c = Controle.objects.filter(matricule=m.matricule).first()
            else:
                c = Controle()
            nom, postnom, prenom  = m.nom or "", m.postnom or "", m.prenom or "" 

            c.id = n + int(m.matricule)
            c.noms = nom + " " + postnom + " " + prenom
            c.unite = self.unite.name
            c.liste_cmd = True
            c.sexe = m.sexe
            c.grade = m.grade
            c.matricule = m.matricule
            c.chef_equipe = self.ce
            c.charge_mission = self.cm

            rp = Person.objects.filter(currentnrnew=m.matricule)
            if rp.exists():
                p = rp.first()

                try :
                    fingerObj4 = Fingerprint.objects.filter(uuid=p, finger_id=4).first()
                    fingerObj = Fingerprint.objects.filter(uuid=p, finger_id=7).first()
                    
                except Fingerprint.DoesNotExist:
                    fingerObj4 = None
                    fingerObj = None
                
                try:
                    faceObj = Face.objects.get(uuid=p)
                except Face.DoesNotExist:
                    faceObj = None

                if fingerObj4 is not None : c.fingerprint4 = binascii.hexlify(fingerObj4.image).decode('utf-8') 
                if fingerObj is not None : c.fingerprint = binascii.hexlify(fingerObj.image).decode('utf-8')
                if faceObj is not None : c.face = binascii.hexlify(faceObj.data).decode('utf-8')

            if m.is_cmd == True:
                c.is_cmd = True
            
            c.mission = self.mission
            c.save()
        
        self.unite.signature = self.signature
        self.unite.save()
        if len(self.signature) > 0 : LogUser(user=self.user,action=f"Confirmation liste commandant de l'unité : {self.unite.name}.").save()    


class Synchronisation(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.etat = False
    
    def run(self):
        self.etat = True
        fsynchronization()
        self.etat = False
        print("FIN SYNCHRO")

def statesynchro():
    controles = Controle.objects.filter(is_synchronize=False, is_controle=True)
    documents = Document.objects.filter(is_synchronize=False)
    logs = LogUser.objects.filter(is_synchronize=False)

    nc = 0
    if controles.exists() and controles : nc = len(controles)

    nd = 0
    if documents.exists() and documents : nd = len(documents)
    
    nl = 0
    if logs.exists() and logs : nl = len(logs)
    nt = nc + nd + nl
    return nt

def fsynchronization():
    controles = Controle.objects.filter(is_synchronize=False, is_controle=True)
    documents = Document.objects.filter(is_synchronize=False)
    logs = LogUser.objects.filter(is_synchronize=False)

    ip = "http://192.168.2.12:80"
    # try:
    #     ip = readIP()
    # except FileNotFoundError:
    #     pass    

    endpoint = ip + "/api/scontrole/"
    for controle in controles:
        if controle.is_controle == False:
            pass
        else:    
            serializer = ControleSerializer(controle)
            
            try:
                r = requests.post(endpoint, json=serializer.data)
                if r.status_code == status.HTTP_200_OK :
                    controle.is_synchronize = True
                    controle.save()

            except Exception as e:
                print("Erreur de synchronisation controle :", e)
    
    endpoint = ip + "/api/sdocuments/"
    
    for d in documents:
        with open(d.image_url.path, "rb") as img:
            b = base64.b64encode(img.read())
        data = {
            'controle': d.controle.id,
            'image_url': b,
            'title': d.title,
            'id':d.id
        }
        try:
            r = requests.post(endpoint, data=data)
            if r.status_code == status.HTTP_200_OK or r.status_code == status.HTTP_201_CREATED :
                d.is_synchronize = True
                d.save()
        except Exception:
            print("Erreur de synchronisation  documents")
    
    endpoint = ip + "/api/logusers/"
    for log in logs:    
        serializer_log = LoguserSerializer(log)
        try:
            r = requests.post(endpoint, json=serializer_log.data)
            if r.status_code == status.HTTP_200_OK :
                log.is_synchronize = True
                log.save()
        except Exception:
            print("Erreur de synchronisation de logs :", Exception)
    return True

def reinitialisation_controle():

    for controle in Controle.objects.all():
        if controle.present == False: 
            controle.is_controle = False
            controle.is_synchronize = False
            controle.save()
            update_retrievelist(controle, False)

class UnionCmd(threading.Thread):
    def __init__(self, user, unite, ce, cm, mission):
        self.user = user
        self.unite = unite
        self.ce = ce
        self.cm = cm
        self.mission = mission
        threading.Thread.__init__(self)
    
    def run(self):

        liste_cmd = RetrieveList.objects.filter(unite=self.unite.name)

        n = self.user.id * 1000000000000
        
        for m in liste_cmd:
            if Controle.objects.filter(matricule=m.matricule).exists():
                c = Controle.objects.filter(matricule=m.matricule).first()
            else:
                c = Controle()
                c.id = n + int(m.matricule)
            nom, postnom, prenom  = m.nom or "", m.postnom or "", m.prenom or ""
            
            if c.liste_cmd == False:
                c.noms = nom + " " + postnom + " " + prenom
                c.unite = self.unite.name
                c.liste_cmd = True
                c.sexe = m.sexe
                c.grade = m.grade
                c.matricule = m.matricule
                c.chef_equipe = self.ce
                c.charge_mission = self.cm

                rp = Person.objects.filter(currentnrnew=m.matricule)
                if rp.exists():
                    p = rp.first()

                    try :
                        fingerObj4 = Fingerprint.objects.filter(uuid=p, finger_id=4).first()
                        fingerObj = Fingerprint.objects.filter(uuid=p, finger_id=7).first()
                        
                    except Fingerprint.DoesNotExist:
                        fingerObj4 = None
                        fingerObj = None
                    
                    try:
                        faceObj = Face.objects.get(uuid=p)
                    except Face.DoesNotExist:
                        faceObj = None

                    if fingerObj4 is not None : c.fingerprint4 = binascii.hexlify(fingerObj4.image).decode('utf-8') 
                    if fingerObj is not None : c.fingerprint = binascii.hexlify(fingerObj.image).decode('utf-8')
                    if faceObj is not None : c.face = binascii.hexlify(faceObj.data).decode('utf-8')

                    if p.unit == self.unite.name : c.base_donnee = True 

                    if m.is_cmd == True:
                        c.is_cmd = True
                c.is_synchronize = False
                c.mission = self.mission
                c.save()

def donwload():
    
    ip = "http://192.168.2.12"
    password = "rdc@2022" 
    username = input("USER :")

    print("Veuillez choisir :")
    print("1. Toutes les unites")
    print("2. Une unité")
    choix = input("Tapez un 1 ou 2 : ")
    unite_c = ""
    if choix == "2":
        unite_considere = "un"
        unite_c = input("Le nom de l'unité : ") 
    else :
        unite_considere = "tous"
     

    client = Client()
    client.go(ip,username,password)

    user_data = client.get_user()

    print("*********USER DATA*********")
    print(user_data)
    
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
            print("*********MISSION*********")
            print(missions)
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
            
            #Equipes
            equipes = client.get_equipes()
            print("*********EQUIPES*********")
            print(equipes)

            if type(equipes)==list and len(equipes) > 0:
                for equipe in equipes:
                    if not Equipe.objects.filter(id=equipe['id']).exists():
                        mission = Mission.objects.get(pk=equipe['mission'])
                        e = Equipe()
                        e.id = equipe['id']
                        e.user = user
                        e.mission = mission
                        e.save()
                    
                    #Controleurs
                    controleurs = equipe.get('controleur')
                    print("*********CONTROLEURS*********")
                    print(controleurs)
                    
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
            print("*********UNITES*********")
            
            if type(unites)==list and len(unites) > 0:
                print(len(unites))
                
                if unite_considere == "tous" :
                    pass
                else:
                    for i in unites:
                        if i.get('name') == unite_c:
                            print("Unité ",i.get('name'))
                        else:
                            unites.remove(i) 
                
                for u in unites:
                    rs_unite = Unite.objects.filter(id=u.get('id'),name=u.get('name'))
                    print("UNITE : ", u.get('name'))
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
                    
                    dmissionunite = u.get('missionunites')
                    if dmissionunite :
                        missionunite = MissionUnite()
                        missionunite.id = dmissionunite.get('id')
                        missionunite.mission_id = dmissionunite.get('mission')
                        missionunite.unite_id = dmissionunite.get('unite')
                        if not MissionUnite.objects.filter(id=dmissionunite.get('id')).exists():
                            missionunite.save()
                        else:
                            missionunite = MissionUnite.objects.filter(id=dmissionunite.get('id')).first()
                        print("*********MISSION UNITES*********")
                        print(missionunite)

                    # Persons
                    dpersons = client.get_datapersons(u.get('id'))
                    print("*********PERSONS*********")
                    if dpersons : print(len(dpersons))
                    persons = []
                    if dpersons != None : persons = dpersons.get('detail')
                    if type(persons)==list and len(persons) > 0:
                        pi = 1
                        for person in persons:
                            if not Person.objects.filter(uuid=person.get('uuid')).exists():
                                print("Person=====>",pi, " : ",person.get('name'))
                                pi = pi + 1
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
                                print("*********DETAIL UNITES*********")
                                print(de)
                            

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
                                print("*********EQUIPE UNITES*********")
                                print(du)

def download_militaire():
    ip = "http://192.168.2.12"
    password = "rdc@2022" 
    username = input("USER :")

    client = Client()
    client.go(ip,username,password)

    user_data = client.get_user()

    print("*********USER DATA*********")
    print(user_data)
    
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

        dmilitaires = client.get_militaires()
        militaires = []
        if dmilitaires != None : militaires = dmilitaires.get('detail')
        if type(militaires)==list and len(militaires) > 0:
            total = len(militaires)
            i = 0
            for militaire in militaires:
                i += 1
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
                    p = round((i * 100)/total)
                    print(p," %")
    print("*********FIN TELECHARGEMENT*********")

#Profile
CTR = 1
CE = 2
CM = 3
MANAGER = 4


def save_user(username,profile):
    user = User(
        username = username.upper(),
        noms = username,
        profile = Profile.objects.get(pk=profile),
        is_active = True
    )
    password = "rdc@2022"
    user.set_password(password)
    user.save()
    return user

def save_equipe():
    for equipe in EQUIPES:
        username = equipe["code"]
        cm = "CM" + username[2:]
        ctr = "CTR" + username[2:]
        numero = username[2:]

        rs_user = User.objects.filter(username=username)
        rs_cm = User.objects.filter(username=cm)
        rs_ctr = User.objects.filter(username=ctr)

        if rs_cm.exists():
            user_cm = rs_cm.first()
        else:
            user_cm = save_user(cm,CM)
            print("NON", user_cm)
        
        new_m = Mission(
            charge_mission = user_cm,
            zone = "ZONE"+cm,
            numero=numero
        )
        if Mission.objects.filter(numero=numero).exists(): 
            m = Mission.objects.filter(numero=numero).first()
        else:
            m = new_m.save()
        print("OK",rs_cm.first(),"CM")

        if rs_user.exists():
            user_ce = rs_user.first()
            m = Mission.objects.get(numero=numero)
            new_e = Equipe(
                user = user_ce,
                mission = m
            )
            if Equipe.objects.filter(user_id=user_ce.id).exists(): 
                e = Equipe.objects.filter(user_id=user_ce.id).first()
            else:
                e = new_e.save()
            print("OK",rs_user.first(),"CE")
        else:
            user = save_user(username,CE)
            print("NON", user)
        
        if rs_ctr.exists():
            print("OK",rs_ctr.first(),"CTR")
        else:
            user = save_user(ctr,CTR)
            print("NON", user)