FILE_TYPE = [
        ("csv","csv"),
        ("xlsx","xlsx")
    ]

categories = [
    'Officier Général',
    'Officier Supérieur',
    'Officier Subalterne',
    'Sous officier 1ere classe',
    'Sous officier 2eme-3eme Classe',
    'Troupe'
]

grades = [
    'Gen',
    'Colonnel',
    'LtCol',
    'Maj',
    'Capt',
    'Lt',
    'SLt',
    'AdjChef',
    'Adj1Cl',
    'Adj',
    '1SgtMaj',
    'SgtMaj',
    '1Sgt',
    'Sgt',
    'Cpl',
    'Sdt1Cl',
    'Sdt2Cl',
    'RECRUE',
    'PerCiv'
]

justifications = [
    'Mission',
    'Formation et stage',
    'En arrière de rejoindre(En route)',
    'Retraite',
    'Décédé',
    'En route',
    'Detenu',
    'Detaché'
]

import json
from django.conf import settings
from qrcode import *
from django.core.files import File

from app_base.models import GrandeUnite, Home, Militaire, Person, Unite
from app_base.utils import getnombreanomalie, getnombrepresentunite, getnunitecontrole
from app_controle.models.controle import Controle, RetrieveList
from app_controle.models.equipe import Equipe
from app_controle.models.document import Document
from app_controle.utils import getUniteByEquipe
from django.db.models import Q

def remove_fields(data, fields):
    for field in fields:
        if field in data:
            del data[field]

    return data

def generate_qrcode(controle, mission):
    rm = Militaire.objects.filter(currentnrnew=controle.matricule)
    if rm.exists():
        m = rm.first()
        prenom = "X"
        if len(m.firstname) > 0 and m.firstname:
            prenom = m.firstname
        data = {
            "matricule": controle.matricule,
            "nom": m.name,
            "prenom": prenom,
            "postnom": m.postname,
            "genre": "MASCULIN" if m.sex== 'M' else "FEMININ",
            "grade": controle.grade,
            "unite": controle.unite,
            "groupeSanguin": m.bloodtype,
            "dateNaissance": m.birthdate.strftime("%d-%m-%Y") or "",
            "lieuNaissance": m.birthplace,
            "zone": mission.zone
        }
        info = json.dumps({"data":data})
        img = make(info)
        img_name = m.uuid + '.png'
        img_path = settings.MEDIA_ROOT + '/face/' + img_name
        img.save(img_path)

        temp_file = open(img_path, 'rb')
        myfile = File(temp_file, name=img_name)

        return myfile

def generate_signature(user, date):
    signature = 'ABA'
    for l in user:
        signature = signature + '-' + str(ord(l))
    
    signature = signature + '-' + str(date.strftime("%Y%m%d"))
    return signature

def findparent(unite):
    liste = unite.name.split("/")
    liste.pop()
    parent = None
    for elt in liste:
        rs_gu = GrandeUnite.objects.filter(name=elt)
        if parent == None:
            if rs_gu.exists():
                gu = rs_gu.first()
            else:
                gu = GrandeUnite()
                gu.name = elt           
                gu.save()
        else:
            if rs_gu.exists():
                gu = rs_gu.first()
            else:
                gu = GrandeUnite()
                gu.name = elt
            gu.parent = parent
            gu.save()
        parent = gu
    unite.parent = parent
    unite.save()

def effectif(unite):
    n = {}
    n["ea"] = 0
    n["ec"] = 0
    n["ecp"] = 0
    n["ej"] = 0
    n["ef"] = 0
    n["container"] = True

    if isinstance(unite, Unite):
        e = Person.objects.filter(unit=unite.name).count()
    
        c = Controle.objects.filter(unite=unite.name).count()
        
        cp = Controle.objects.filter(unite=unite.name,present=True).count()
        
        j = Controle.objects.filter(unite=unite.name,present=False,justifie=True).count()
        
        n["ea"] = n["ea"] + e
        n["ec"] = n["ec"] + c
        n["ecp"] = n["ecp"] + cp
        n["ej"] = n["ej"] + j
        n["ef"] = n["ef"] + (c - cp - j)
        n["container"] = False
        
        return n
    else:        
        children = unite.children.all()
        
        if not children:
            unites = Unite.objects.filter(parent=unite)
            for unite in unites:
                e = Person.objects.filter(unit=unite.name).count()
    
                c = Controle.objects.filter(unite=unite.name).count()
                
                cp = Controle.objects.filter(unite=unite.name,present=True).count()
                
                j = Controle.objects.filter(unite=unite.name,present=False,justifie=True).count()
                
                n["ea"] = n["ea"] + e
                n["ec"] = n["ec"] + c
                n["ecp"] = n["ecp"] + cp
                n["ej"] = n["ej"] + j
                n["ef"] = n["ef"] + (c - cp - j)

            return n
        else:
            for child in children:
                e = effectif(child)
                n["ea"] = n["ea"] + e["ea"]
                n["ec"] = n["ec"] + e["ec"]
                n["ecp"] = n["ecp"] + e["ecp"]
                n["ej"] = n["ej"] + e["ej"]
                n["ef"] = n["ef"] + e["ef"]

    return n

def update_retrievelist(controle=None,is_controle=True):
    controles = None
    if controle == None:
        controles = Controle.objects.filter(is_controle=True)
        for c in controles:
            rs_rl = RetrieveList.objects.filter(matricule=c.matricule)
            if rs_rl.exists():
                rl = rs_rl.first()
                rl.is_controle = is_controle
                rl.save()
    else:
        rs_rl = RetrieveList.objects.filter(matricule=controle.matricule)
        if rs_rl.exists():
            rl = rs_rl.first()
            rl.is_controle = is_controle
            rl.save()

def update_home(user):
    print("MISE A JOUR DES STATISTIQUES")
    if user != None : profile = user.profile_id
    equipes = Equipe.objects.all()

    if user == None:
        controles = Controle.objects.all()
        persons = Person.objects.all()
        unites = Unite.objects.all()
    
    elif user.is_superuser or profile == 4:
        controles = Controle.objects.all()
        persons = Person.objects.all()
        unites = Unite.objects.all()
            
    else:
        unites = getUniteByEquipe(user)
        queryc = Q(id=0)
        queryp = Q(id=0)
        for index, unite in enumerate(unites):
            if index == 0:
                queryc = Q(unite=unite.name)
                queryp = Q(unit=unite.name)
            else:
                queryc.add(Q(unite=unite.name), Q.OR)
                queryp.add(Q(unit=unite.name), Q.OR)
        controles = Controle.objects.filter(queryc)
        persons = Person.objects.filter(queryp)

    nperson = persons.count()
    print("MILITAIRES :", nperson)
    
    nunite = unites.count()
    print("UNITES :", nunite)

    nequipe = equipes.count()
    print("EQUIPES :", nequipe)

    ncontrole = controles.count()
    print("CONTROLES :", ncontrole)

    nunitecontrole = getnunitecontrole(controles)
    print("UNITES CONTROLES :", nunitecontrole)

    ncontrolepresent = controles.filter(present=True).count()
    #ncontrolepresent = controles.filter(base_donnee=True, present=True).count()
    print("CONTROLES PRESENTS :", ncontrolepresent)

    ncontrolepresentunite = getnombrepresentunite(controles)
    print("CONTROLES PRESENTS A L'UNITE :", ncontrolepresentunite)

    ncontrolepresentnouveau = ncontrolepresent - ncontrolepresentunite
    print("CONTROLES PRESENTS NOUVEAU :", ncontrolepresentnouveau)

    ncontrolejustifie = controles.filter(justifie=True).count()
    print("CONTROLES JUSTIFIES :", ncontrolejustifie)

    ncontroleanomalie = getnombreanomalie(controles)
    print("CONTROLES ANOMALIES :", ncontroleanomalie)

    ncontrolejustifiesimple = ncontrolejustifie - ncontroleanomalie
    print("CONTROLES JUSTIFIES NORMALES :", ncontrolejustifiesimple)

    nnonjustifie = ncontrole - (ncontrolepresent + ncontrolejustifie)
    print("CONTROLES NON JUSTIFIES :", nnonjustifie) 

    home = Home()
    home.total_bdd = nperson
    home.total_unite = nunite
    home.total_equipe = nequipe
    home.total_inscrit = ncontrole
    home.total_present = ncontrolepresent
    home.total_present_unite = ncontrolepresentunite
    home.total_present_nouveau = ncontrolepresentnouveau
    home.total_justifie = ncontrolejustifie
    home.total_justifie_normal = ncontrolejustifiesimple
    home.total_justifie_anomalie = ncontroleanomalie
    home.total_non_justifie = nnonjustifie
    home.total_unite_controle = nunitecontrole
    home.save()

    print("MISE A JOUR TERMINEE")

def get_next_or_prev(models, item, direction):
    '''
    Returns the next or previous item of
    a query-set for 'item'.

    'models' is a query-set containing all
    items of which 'item' is a part of.

    direction is 'next' or 'prev'
    
    '''

    getit = False
    if direction == 'prev':
        models = models.reverse()
    for m in models:
        if getit:
            return m
        if item == m:
            getit = True
    if getit:
        # This would happen when the last
        # item made getit True
        return models[0]
    return False

def cleaning():
    controles = Controle.objects.all()
    presents = controles.filter(present=True)
    justifies = controles.filter(justifie=True)

    presents_matricules = presents.values('matricule')
    justifies_matricules = justifies.values('matricule')

    autres = controles.exclude(id__in=presents).exclude(id__in=justifies)

    for a in autres:
        d = {
            'matricule' : a.matricule
        }
        if d in presents_matricules or d in justifies_matricules:
            print("DOUBLONS",a.matricule," : ",a.noms)
            a.delete()
    
    for j in justifies:
        d = {
            'matricule' : j.matricule
        }
        if d in presents_matricules:
            print("DOUBLONS",j.matricule," : ",j.noms)
            j.delete()

    n = 0  
    nautres = autres.values('matricule')
    listes = list()
    for a in autres:
        d = {
            'matricule':a.matricule
        }
        if d in listes:
            n += 1
            a.delete()
        else:
            listes.append(d)
    
    print(len(listes))
    print(n)

    nj = 0  
    listesj = list()
    for j in justifies:
        d = {
            'matricule':j.matricule
        }
        if d in listesj:
            nj += 1
            j.delete()
        else:
            listesj.append(d)
    
    print(len(listesj))
    print(nj)

def correction():
    controles = Controle.objects.all()
    for controle in controles:
        if controle.present == False and controle.justifie == False:
            rs = Document.objects.filter(controle_id=controle.id)
            if rs.exists():
                document = rs.first()
                if document.title == 'En arrière de rejoindre(En route)':
                    controle.present = True
                    controle.save()
                    print("=>",controle.matricule," : ",controle.noms)
                if document.title == 'Décédé':
                    controle.justifie = True
                    controle.save()
                    print("Décédé",controle.matricule," : ",controle.noms)
                

        