from app_base.models import Person, Unite
import app_controle
from django.db.models import Q
from app_controle.models.controle import Controle
from app_controle.models.equipe_unite import EquipeUnite
from app_controle.models.equipe import Equipe
from app_controle.models.listing import BddnoListing, Listing, ListingnoBdd

from app_controle.serializers import ArhiveDetailSerializer, ControleSerializer, UniteSerializer
from app_user.models import User
import operator

def remove_fields(data, fields):
    for field in fields:
        if field in data:
            del data[field]

    return data

def getUniteByUser(user):
    detail_unites = user.detailunite_set.all()
    unites = []
    for d in detail_unites:
        is_confirm = False
        if d.unite.signature and len(d.unite.signature) > 0:
            is_confirm = True
        unites.append({"unite_id":d.unite.id,"unite_name":d.unite.name,"is_confirm":is_confirm})
    return unites

def uniteByUser(id_user):
    user = User.objects.get(pk=id_user)
    id_profile = user.profile_id
    if id_profile == 2:
        return getUniteByEquipe(user)
    elif id_profile == 3:
        return getUniteByMission(user)
    else:
        return Unite.objects.all()

def getUniteByEquipe(user):
    unites = []
   
    equipe = Equipe.objects.get(user=user)
    equipe_unites = EquipeUnite.objects.filter(equipe=equipe)

    if len(equipe_unites) > 0:
        for d in equipe_unites:
            unites.append(d.unite)
     
    return unites

def getUniteByMission(user):
    unites = []
    try:
        mission = app_controle.models.Mission.objects.get(charge_mission_id=user.id)

        mission_unites = mission.missionunite_set.all()
        for d in mission_unites:
            unites.append(d.unite)
    except Exception:
        pass
    return unites

def getPersonByUser(user):
    unites = getUniteByUser(user)
    for index, unite in enumerate(unites):
        if index == 0:
            query = Q(unit=unite['unite_name'])
        else:
            query.add(Q(unit=unite['unite_name']), Q.OR)
    queryset = Person.objects.filter(query)
    return queryset

def getPersonByUserUnite(user, id_unite):
    unites = getUniteByUser(user)
    
    queryset = Person.objects.filter(name="z")
    
    for unite in unites:
        if unite['unite_id'] == id_unite:
            queryset = Person.objects.filter(unit=unite['unite_name'])
    return queryset

def getPersonFromCmd(user):
    unites = getUniteByUser(user)
    for index, unite in enumerate(unites):
        if index == 0:
            query = Q(unite=unite['unite_name'])
        else:
            query.add(Q(unite=unite['unite_name']), Q.OR)
    queryset = app_controle.models.RetrieveList.objects.filter(query)
    return queryset

def getPersonFromCmdUnite(user, id_unite):
    unites = getUniteByUser(user)
    queryset = app_controle.models.RetrieveList.objects.filter(unite="z")
    for unite in unites:
        if unite['unite_id'] == id_unite:
            queryset = app_controle.models.RetrieveList.objects.filter(unite=unite['unite_name'])
    return queryset

def getPersonByEquipe(user):
    unites = getUniteByEquipe(user)
    query = Q(unit="")
    for index, unite in enumerate(unites):
        if index == 0:
            query = Q(unit=unite.name)
        else:
            query.add(Q(unit=unite.name), Q.OR)
    personnes = Person.objects.filter(query)
    return personnes

def getPersonByMission(user):
    unites = getUniteByMission(user)
    query = Q(unit="")
    for index, unite in enumerate(unites):
        if index == 0:
            query = Q(unit=unite.name)
        else:
            query.add(Q(unit=unite.name), Q.OR)
    personnes = Person.objects.filter(query)
    return personnes

def getEquipe(user):
    profile = None
    equipes = []
    try:
        profile = user.profile
    except Exception:
        profile = None

    if profile.id == 2:
        equipes = app_controle.models.Equipe.objects.filter(user=user)
    if profile.id == 3:
        try:
            mission = app_controle.models.Mission.objects.filter(charge_mission=user)
        except app_controle.models.Mission.DoesNotExist:
            mission = None
        
        if mission != None :
            equipes = app_controle.models.Equipe.objects.filter(mission=mission)
    
    return equipes

def getMission(user):
    equipes = getEquipe(user)
    mission = None
    equipe = equipes[0]

    mission = app_controle.models.Mission.objects.filter(id=equipe.mission.id)

    return mission

def getControleur(user):
    profile = None
    users = []
    try:
        profile = user.profile
    except Exception:
        profile = None
    
    if profile.id == 2:
        equipes = app_controle.models.Equipe.objects.filter(user=user)
    if profile.id == 3:
        try:
            mission = app_controle.models.Mission.objects.filter(charge_mission=user)
        except app_controle.models.Mission.DoesNotExist:
            mission = None
        
        if mission != None :
            equipes = app_controle.models.Equipe.objects.filter(mission=mission)
    
    return users

def confirmIP(ip):
    file = open("ip.txt", "w")
    file.write(ip)
    file.close()

def readIP():
    file = open("ip.txt", "r")
    ip = file.read()
    file.close()
    return ip

def archiver_mission(id_mission):
    mission = app_controle.models.Mission.objects.get(id=id_mission)
    if mission is None:
        pass
    else:
        rs_archive = app_controle.models.Archive.objects.filter(numero=mission.numero)
        if rs_archive.exists():
            pass
        else:
            archive = app_controle.models.Archive()
            archive.date_debut = mission.date_debut
            archive.date_fin = mission.date_fin
            archive.charge_mission = mission.charge_mission
            archive.zone = mission.zone
            archive.numero = mission.numero
            archive.id = mission.id
            archive.save()
            
            controles = app_controle.models.Controle.objects.filter(mission_id=id_mission)

            for controle in controles:
                
                init_data = ControleSerializer(controle).data

                id = init_data.get('id')
                person = Person.objects.get(uuid=init_data.get('person')) or None
                ctl = User.objects.filter(id=init_data.get('controleur')).first() or None
                se = app_controle.models.Seance.objects.filter(id=controle.seance_id).first() or None
                ce = User.objects.get(id=init_data.get('chef_equipe')) or None
                cm = User.objects.get(id=init_data.get('charge_mission')) or None

                
                exclude_fields = ['person', "controleur", "mission", "seance", "chef_equipe", "charge_mission","documents"]
                data = remove_fields(init_data, exclude_fields)

                ad = app_controle.models.ArchiveDetail(**data)
                ad.archive = archive
                ad.person = person
                ad.controleur = ctl
                ad.seance = se
                ad.chef_equipe = ce
                ad.charge_mission = cm
                ad.id_controle = id
                ad.save()
                controle.delete()

def restaurer_mission(id_archive):
    archive = app_controle.models.Archive.objects.get(id=id_archive)
    if archive is None:
        pass
    else:
        ads = app_controle.models.ArchiveDetail.objects.filter(archive_id=id_archive)
        for ad in ads:
            data = ArhiveDetailSerializer(ad).data

            person = Person.objects.get(uuid=data.get('person')) or None
            ctl = User.objects.filter(id=data.get('controleur')).first() or None
            se = app_controle.models.Seance.objects.filter(id=data.get('seance')).first() or None
            ce = User.objects.get(id=data.get('chef_equipe')) or None
            cm = User.objects.get(id=data.get('charge_mission')) or None

            exclude_fields = ['archive','id_controle', 'person', "controleur", "mission", "seance", "chef_equipe", "charge_mission"]
            data = remove_fields(data, exclude_fields)

            c = app_controle.models.Controle(**data)
            c.person = person
            c.person = person
            c.controleur = ctl
            c.seance = se
            c.chef_equipe = ce
            c.charge_mission = cm
            c.save()
            ad.delete()
        archive.delete()

PROVINCES = [
    {"code":"KIN","titre":"KINSHASA"},
    {"code":"KCE","titre":"KONGO CENTRAL, EQUATEUR, BANDUNDU"},
    {"code":"31RGN","titre":"31RGN"},
    {"code":"32RGN","titre":"32RGN"},
    {"code":"33RGN","titre":"33RGN"},
    {"code":"34RGN","titre":"34RGN"},
    {"code":"2ZDEF","titre":"2ZDEF"},
]

EQUIPEZ1 = [
    {"code":"CE","titre":"EQUIPE"}
]

EQUIPEZDEF1 = [
    {"code":"CE1","titre":"EQUIPE 1"},
    {"code":"CE2","titre":"EQUIPE 2"},
    {"code":"CE3","titre":"EQUIPE 3"},
    {"code":"CE4","titre":"EQUIPE 4"},
    {"code":"CE5","titre":"EQUIPE 5"},
    {"code":"CE6","titre":"EQUIPE 6"},
    {"code":"CE7","titre":"EQUIPE 7"},
    {"code":"CE8","titre":"EQUIPE 8"},
    {"code":"CE9","titre":"EQUIPE 9"},
    {"code":"CE10","titre":"EQUIPE 10"},
    {"code":"CE11","titre":"EQUIPE 11"},
    {"code":"CE12","titre":"EQUIPE 12"},
    {"code":"CE13","titre":"EQUIPE 13"},
    {"code":"CE14","titre":"EQUIPE 14"},
    {"code":"CE15","titre":"EQUIPE 15"},
    {"code":"CE16","titre":"EQUIPE 16"},
    {"code":"CE17","titre":"EQUIPE 17"},
]

EQUIPEZDEF2 = [
    {"code":"CE19","titre":"EQUIPE 19"},
    {"code":"CE20","titre":"EQUIPE 20"},
    {"code":"CE21","titre":"EQUIPE 21"},
    {"code":"CE22","titre":"EQUIPE 22"},
    {"code":"CE23","titre":"EQUIPE 23"},
    {"code":"CE24","titre":"EQUIPE 24"},
    {"code":"CE25","titre":"EQUIPE 25"},
    {"code":"CE26","titre":"EQUIPE 26"},
    {"code":"CE27","titre":"EQUIPE 27"},
    {"code":"CE28","titre":"EQUIPE 28"},
    {"code":"CE29","titre":"EQUIPE 29"},
]

EQUIPEZDEF3 = [
    {"code":"CE30","titre":"EQUIPE 30"},
    {"code":"CE31","titre":"EQUIPE 31"},
    {"code":"CE32","titre":"EQUIPE 32"},
    {"code":"CE34","titre":"EQUIPE 34"},
    {"code":"CE35","titre":"EQUIPE 35"},
    {"code":"CE36","titre":"EQUIPE 36"},
    {"code":"CE37","titre":"EQUIPE 37"},
    {"code":"CE38","titre":"EQUIPE 38"},
    {"code":"CE39","titre":"EQUIPE 39"},
    {"code":"CE40","titre":"EQUIPE 40"},
    {"code":"CE41","titre":"EQUIPE 41"},

    {"code":"CE42","titre":"EQUIPE 42"},
    {"code":"CE43","titre":"EQUIPE 43"},
    {"code":"CE44","titre":"EQUIPE 44"},
    {"code":"CE45","titre":"EQUIPE 45"},
    {"code":"CE46","titre":"EQUIPE 46"},
    {"code":"CE47","titre":"EQUIPE 47"},
    {"code":"CE48","titre":"EQUIPE 48"},
    {"code":"CE49","titre":"EQUIPE 49"},


    {"code":"CE50","titre":"EQUIPE 50"},
    {"code":"CE51","titre":"EQUIPE 51"},
    {"code":"CE52","titre":"EQUIPE 52"},
    {"code":"CE53","titre":"EQUIPE 53"},
    {"code":"CE54","titre":"EQUIPE 54"},
    {"code":"CE55","titre":"EQUIPE 55"},
    {"code":"CE56","titre":"EQUIPE 56"},
    {"code":"CE57","titre":"EQUIPE 57"},
    {"code":"CE58","titre":"EQUIPE 58"},
    {"code":"CE59","titre":"EQUIPE 59"},
    {"code":"CE60","titre":"EQUIPE 60"},
    #{"code":"CE61","titre":"EQUIPE 61"},
    {"code":"CE62","titre":"EQUIPE 62"},
    {"code":"CE63","titre":"EQUIPE 63"},
    {"code":"CE64","titre":"EQUIPE 64"},
]

EQUIPES = EQUIPEZDEF1 + EQUIPEZDEF2 + EQUIPEZDEF3
EQUIPES = EQUIPEZ1


def statistiques_provinciales():
    provinces = PROVINCES
    
    data = list()
    for pro in provinces:
        missions = app_controle.models.Mission.objects.filter(zone__contains=pro["code"])
        efAtt = 0
        effCtr = 0
        effjs = 0
        effNonjus = 0

        for mission in missions:
            uniteMs = mission.missionunite_set.all()
            queryp = Q(uuid=0)
            for index, unm in enumerate(uniteMs):
                unite = unm.unite
                if index == 0:
                    queryp = Q(unit=unite.name)
                else:
                    queryp.add(Q(unit=unite.name), Q.OR)
            
            efAtt += Person.objects.filter(queryp).count()

            effCtr += app_controle.models.Controle.objects.filter(mission=mission,present=True).count()

            effjs += app_controle.models.Controle.objects.filter(mission=mission,present=False,justifie=True).count()
                
        effNonjus =   efAtt - (effCtr + effjs)
        data.append(
            {
                "province":pro["titre"],
                "effatt" : efAtt,
                "effctr" :  effCtr,
                "effjs" : effjs,
                "effnjs": effNonjus  
            }
        )
        
    return data

def statistiques_KCE():
    provinces = EQUIPES
    
    data = list()
  
    equipes = app_controle.models.Equipe.objects.filter(mission__zone="KCE",user__name="CE22") | app_controle.models.Equipe.objects.filter(mission__zone="KCE",user__name="CE23") |  app_controle.models.Equipe.objects.filter(mission__zone="KCE",user__name="CE23")
       
        
    for e in equipes:
        missions = app_controle.models.Mission.objects.filter(zone__contains="KCE")
        
        efAtt = 0
        effCtr = 0
        effjs = 0
        effNonjus = 0

        for mission in missions:
            uniteMs = mission.missionunite_set.all()
            queryp = Q(uuid=0)
            for index, unm in enumerate(uniteMs):
                unite = unm.unite
                if index == 0:
                    queryp = Q(unit=unite.name)
                else:
                    queryp.add(Q(unit=unite.name), Q.OR)
            
            efAtt += Person.objects.filter(queryp).count()

            effCtr += app_controle.models.Controle.objects.filter(mission=mission,present=True).count()

            effjs += app_controle.models.Controle.objects.filter(mission=mission,present=False,justifie=True).count()
                
        effNonjus =   efAtt - (effCtr + effjs)
        data.append(
            {
                "province":"KONGO CENTRAL",
                "effatt" : efAtt,
                "effctr" :  effCtr,
                "effjs" : effjs,
                "effnjs": effNonjus  
            }
        )
        
    return data
# import threading 
# class Stat(rhreading.Thread):
#     def __init__(self):
#         threading.Thread.__init__(self)

#     def run(self):
#         statistiques_provinciales()

def statistiques_equipes():
    equipes = EQUIPES
    data = list()
    datas = list()
    ctrp = 0
    ctrj = 0
    ctrnj = 0
    sp = 0
    sj = 0
    snj = 0
    sbdd = 0
    for e in equipes:
        bdd = 0
        username = e['code']
        user = User.objects.get(username=username)
        equipe = Equipe.objects.get(user_id=user.id)
        equipeunites = equipe.equipeunite_set.all()
        for eu in equipeunites:
            u = eu.unite
            bdd = bdd + len(Person.objects.filter(unit=u.name))

        ctrp = Controle.objects.filter(chef_equipe__username=e["code"],present=True).count()
        sp = sp + ctrp
        ctrj = Controle.objects.filter(chef_equipe__username=e["code"],justifie=True).count()
        sj = sj + ctrj
        ctrnj =  Controle.objects.filter(chef_equipe__username=e["code"],justifie=False,present=False).count()
        snj = snj + ctrnj
        sbdd = sbdd + bdd
        data.append(
            {
                "equipe":e["titre"],
                "effctr" :    ctrp ,
                "effjs" :  ctrj,
                "effnjs":  ctrnj,
                "bdd": bdd,
                "equipe":equipe
            }
        )

    datas.append({
        "sp":sp,
        "sj":sj,
        "snj":snj,
        "sbdd":sbdd
    } )  
    return data, sbdd

def statistiques_by_equipes(equipezdef):
    equipes = equipezdef
    data = list()
    datas = list()
    ctrp = 0
    ctrj = 0
    ctrnj = 0
    sp = 0
    sj = 0
    snj = 0
    sbdd = 0
    for e in equipes:
        bdd = 0
        username = e['code']
        user = User.objects.get(username=username)
        equipe = Equipe.objects.get(user_id=user.id)
        equipeunites = equipe.equipeunite_set.all()
        for eu in equipeunites:
            u = eu.unite
            bdd = bdd + len(Person.objects.filter(unit=u.name))

        ctrp = Controle.objects.filter(chef_equipe__username=e["code"],present=True).count()
        sp = sp + ctrp
        ctrj = Controle.objects.filter(chef_equipe__username=e["code"],justifie=True).count()
        sj = sj + ctrj
        ctrnj =  Controle.objects.filter(chef_equipe__username=e["code"],justifie=False,present=False).count()
        snj = snj + ctrnj
        sbdd = sbdd + bdd
        data.append(
            {
                "equipe":e["titre"],
                "effctr" :    ctrp ,
                "effjs" :  ctrj,
                "effnjs":  ctrnj,
                "bdd": bdd,
                "equipe":equipe
            }
        )

    datas.append({
        "sp":sp,
        "sj":sj,
        "snj":snj,
        "sbdd":sbdd
    } )  
    return data, sbdd

def croisement_bdd_listing():
    bdd = Person.objects.all()
    listing = Listing.objects.all()

    n_bdd = bdd.count()
    n_listing = listing.count()

    print("LISTING vers BDD")
    print("****************")
    i = 0
    for m in listing:
        i += 1
        matricule = m.matricule
        rs_l = Person.objects.filter(currentnrnew=matricule)
        if rs_l.exists():
            pass
        else:
            p = (i * 100) / n_listing
            print("...",p,"%")
            print("MATRICULE "+str(matricule)+" NOMS : "+m.noms)
            l = ListingnoBdd()
            copier(l,m)
            if ListingnoBdd.objects.filter(matricule=l.matricule).exists():
                pass
            else:
                l.save()

    print("BDD vers LISTING")
    print("****************")
    i = 0
    for m in bdd:
        i += 1
        matricule = m.currentnrnew
        rs_l = Listing.objects.filter(matricule=matricule)
        if rs_l.exists():
            pass
        else:
            p = round((i * 100) / n_bdd)
            print("...",p,"%")
            print("MATRICULE "+str(matricule)+" NOMS : "+m.name+m.postname)
            l = BddnoListing()
            l.province = m.province
            l.unite = m.unit 
            l.id_personnel = m.idperso
            
            noms = ""
            if m.name : noms += m.name
            if m.postname : noms += " " + m.postname
            if m.firstname : noms += " " + m.firstname           
            l.noms = noms

            l.matricule = m.currentnrnew 
            l.grade = m.grade 

            if BddnoListing.objects.filter(matricule=l.matricule).exists():
                pass
            else:
                l.save()
    
def copier(l,m):
    l.province = m.province
    l.entite = m.entite 
    l.unite = m.unite 
    l.sigle_banque = m.sigle_banque 
    l.banque = m.banque 
    l.id_personnel = m.id_personnel 
    l.noms = m.noms 
    l.noms_beneficiaire = m.noms_beneficiaire 
    l.matricule = m.matricule 
    l.grade = m.grade 
    l.code_adm = m.code_adm 
    l.particularite = m.particularite 
    l.bases = m.bases 
    l.transports = m.transports 
    l.retenue = m.retenue 
    l.autres = m.autres 
    l.ftc = m.ftc 