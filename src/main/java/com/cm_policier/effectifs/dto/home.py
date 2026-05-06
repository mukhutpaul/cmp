from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.contrib.auth.decorators import login_required
from django.core.paginator import Paginator

from app_base.models import Face, Fingerprint, Garnison, Home, Person, Province, Unite
from app_base.thread import HomeThread
from app_base.utils import getnombreanomalie, getnombrepresentunite, getnunitecontrole
from app_controle.forms import ControleRegistraction, DetailequipeRegistraction, DetailuniteRegistraction, DocumentRegistraction, EquipeRegistraction, UserRegistraction
from app_controle.models import AffectationTablette, Controle, DetailEquipe, DetailUnite, Equipe, Mission, MissionTablette, MissionUnite, Tablette
from app_controle.models.document import Document
from app_controle.utils import getPersonByEquipe, getPersonByMission, getUniteByEquipe, getUniteByMission
from app_user.models import User

from datetime import datetime

from django.db.models import Q

from root.utils import update_home

def update_home_view(request):
    id_user = request.user.id
    user = User.objects.get(pk=id_user)
    HomeThread(user).start()
    return HttpResponseRedirect('/')

@login_required
def home(request):
    id_user = request.user.id
    user = User.objects.get(pk=id_user)

    if user.profile and user.profile.id == 2 :
        return HttpResponseRedirect('/home')

    rs_home = Home.objects.all()
    

    if rs_home.exists():
        h = Home.objects.latest('moment')
    else:
        update_home(request.user)
        h = Home.objects.latest('moment')

    pcontrolepresent = 0
    pcontrolejustifie = 0
    pnonjustifie = 0

    if h.total_inscrit > 0 :
        pcontrolepresent = round( h.total_present * 100 / h.total_inscrit )
        pcontrolejustifie = round( h.total_justifie * 100 / h.total_inscrit )
        pnonjustifie = round( h.total_non_justifie * 100 / h.total_inscrit )

    
    ctx = {
        "persons" : h.total_bdd,
        "unites" : h.total_unite,
        "tablettes" : Tablette.objects.count(),
        "equipes" : h.total_equipe ,
        "ncontroles" : h.total_inscrit,
        "nunitecontroles" : h.total_unite_controle,
        "a1" : pcontrolepresent,
        "na1" : h.total_present,
        "njustifie" : h.total_justifie,
        "justifie":pcontrolejustifie,
        "autre" : pnonjustifie ,
        "nautre" : h.total_non_justifie,
        "ldash" : "active",
        "presentunite": h.total_present_unite,
        "nouvelleaffectation": h.total_present_nouveau,
        "anomalie": h.total_justifie_anomalie,
        "jusnormale": h.total_justifie_normal,
        "idgu":0,
        "moment" : h.moment 
    }
    return render(request,'app_base/home.html', ctx)

@login_required
def home2(request):
    user = User.objects.get(pk=request.user.id)
    unites = Unite.objects.all()
    equipe = Equipe.objects.count()
    controles = 0 
    a1 = 0
    nautre = 0
    npersonnes = 0
    ncontroles = 0
    users = []
    na1 = 0
    njustifie = 0
    justifie = 0
    autre = 0
    tablette = 0
    n = 0
    nunitecontroles = 0
    af = AffectationTablette.objects.all()
    datejour = datetime.today().strftime('%Y-%m-%d')
    nbrJ = Controle.objects.filter(created_at__contains=datejour).count()

    presentunite = getnombrepresentunite()

    anomalie = getnombreanomalie()
    
   
    if user.is_superuser or user.profile_id == 4:
        ncontroles = Controle.objects.count()
        nunitecontroles = getnunitecontrole(Controle.objects.all())
        npersonnes = Person.objects.count()
        # na1 = Controle.objects.filter(base_donnee=True,present=True).count()
        na1 = Controle.objects.filter(present=True).count()
        njustifie = Controle.objects.filter(justifie=True).count()
        tablette = Tablette.objects.count()
        equipe = Equipe.objects.count()
        
        nautre = ncontroles - (njustifie + na1)
                    
        if ncontroles > 0:
            a1 = round( na1 * 100 / ncontroles )
            justifie = round( njustifie * 100 / ncontroles )
            autre = round( nautre * 100 / ncontroles )
        else:
            a1 = 0
            justifie = 0
            autre = 0
    else:
        id_profile = user.profile_id
        if id_profile == 2 :
            equipe = Equipe.objects.filter(user=request.user.id).count()
            deq = DetailEquipe.objects.filter(equipe__user_id = request.user.id)
            tablettes_affec = AffectationTablette.objects.all()
            eq = Equipe.objects.get(user=request.user.id)
            de = DetailEquipe.objects.filter(equipe = eq.id)
            ctls = []
            tbts = []

            for d in de:
               ctls.append(d.user)  
               for ct in ctls:
                for tb in tablettes_affec:
                 if tb.user ==  ct:
                    tbts.append(tb) 
                    tablette = len(tbts) 

            for d in deq :
                users.append(d.user)
            n=0
            afftab = AffectationTablette.objects.all()
            for af in afftab:
                for u in users:
                    if u.id == af.user.id:
                        n = n + 1
            unites = getUniteByEquipe(user)
            queryc = Q(id=0)
            queryp = Q(uuid=0)
            queryna1 = Q(id=0)
            queryj =  Q(id=0)
            for index, unite in enumerate(unites):
                if index == 0:
                    queryc = Q(unite=unite.name)
                    queryj = Q(unite=unite.name)
                    queryna1 = Q(unite=unite.name)
                    queryp = Q(unit=unite.name)
                else:
                    queryc.add(Q(unite=unite.name), Q.OR)
                    queryj.add(Q(unite=unite.name), Q.OR)
                    queryna1.add(Q(unite=unite.name), Q.OR)
                    queryp.add(Q(unit=unite.name), Q.OR)
                    
            ncontroles = Controle.objects.filter(queryc).count()
            nunitecontroles = getnunitecontrole(Controle.objects.filter(queryc))
            npersonnes = Person.objects.filter(queryp).count()
            
            #queryna1.add(Q(base_donnee=True), Q.AND)
            #queryna1.add(Q(liste_cmd=True), Q.AND)
            queryna1.add(Q(present=True), Q.AND)
            na1 = Controle.objects.filter(queryna1).count()

            queryj.add(Q(justifie=True), Q.AND)
            njustifie = Controle.objects.filter(queryj).count()
            
            nautre = ncontroles - (njustifie + na1)
            
            if ncontroles > 0:
                a1 = round( na1 * 100 / ncontroles )
                justifie = round( njustifie * 100 / ncontroles )
                autre = round( nautre * 100 / ncontroles )
            else:
                a1 = 0
                justifie = 0
                autre = 0

        if id_profile == 3 :
            m = Mission.objects.get(charge_mission = request.user.id)
            equipe = Equipe.objects.filter(mission = m.id).count()
            tablette = MissionTablette.objects.filter(mission=m.id).count()
            eqs = Equipe.objects.filter(mission=m.id)
            tablettes_affec = AffectationTablette.objects.all()

            ctls = []
            tbts = []
            eq = []
               
            for e in eqs:
              eq.append(e.user)
              de = DetailEquipe.objects.filter(equipe = e.id)
              
              for d in de :
                af = AffectationTablette.objects.filter(user=d.user)
              for f in af:
                tbts.append(f)
                tablette = len(tbts)
            
            deq = DetailEquipe.objects.filter(equipe__user_id = request.user.id)
            for d in deq :
                users.append(d.user)
            n=0
            afftab = AffectationTablette.objects.all()
            for af in afftab:
                for u in users:
                    if u.id == af.user.id:
                        n = n + 1

            unites = getUniteByMission(user)
            queryc = Q(id=0)
            queryp = Q(uuid=0)
            queryna1 = Q(id=0)
            queryj =  Q(id=0)
            
            for index, unite in enumerate(unites):
                if index == 0:
                    queryc = Q(unite=unite.name)
                    queryj = Q(unite=unite.name)
                    queryna1 = Q(unite=unite.name)
                    queryp = Q(unit=unite.name)
                else:
                    queryc.add(Q(unite=unite.name), Q.OR)
                    queryj.add(Q(unite=unite.name), Q.OR)
                    queryna1.add(Q(unite=unite.name), Q.OR)
                    queryp.add(Q(unit=unite.name), Q.OR)
                    
            ncontroles = Controle.objects.filter(queryc).count()
            nunitecontroles = getnunitecontrole(Controle.objects.filter(queryc))
            npersonnes = Person.objects.filter(queryp).count()
            
            #queryna1.add(Q(base_donnee=True), Q.AND)
            #queryna1.add(Q(liste_cmd=True), Q.AND)
            queryna1.add(Q(present=True), Q.AND)
            na1 = Controle.objects.filter(queryna1).count()

            queryj.add(Q(justifie=True), Q.AND)
            njustifie = Controle.objects.filter(queryj).count()
            
            nautre = ncontroles - (njustifie + na1)

            if ncontroles > 0:
                a1 = round( na1 * 100 / ncontroles )
                justifie = round( njustifie * 100 / ncontroles )
                autre = round( nautre * 100 / ncontroles )
            else:
                a1 = 0
                justifie = 0
                autre = 0

                
    ctx = {
        "persons" : npersonnes,
        "unites" : len(unites),
        "tablettes" : tablette,
        "equipes" :equipe ,
        "controles" : controles,
        "ncontroles" : ncontroles,
        "nunitecontroles" : nunitecontroles,
        "a1" : a1,
        "na1" : na1,
        "njustifie" : njustifie,
        "justifie":justifie,
        "autre" : autre,
        "nautre" : nautre,
        "ldash2" : "active",
        "data" : n,
        "nbrctrJ":  nbrJ,
        "presentunite": presentunite,
        "nouvelleaffectation": na1 - presentunite,
        "anomalie": anomalie,
        "jusnormale": njustifie - anomalie,
        "idgu":0 
    }
    return render(request,'app_base/home2.html', ctx)

@login_required
def UniteView(request):
    unites = Unite.objects.all()

    return render(request,'app_base/unites.html',{'unites':unites})

@login_required
def ProvinceView(request):
    provinces = Province.objects.all()

    return render(request,'app_base/provinces.html',{'provinces':provinces})

@login_required
def PersonView(request):
    data = []
    
    id_user = request.user.id
    user = User.objects.get(pk=id_user)

    profile = None

    try:
        profile = user.profile
    except Exception:
        profile = None
    
    if profile != None:

        if profile.id == 4 or user.is_superuser:
            data = Person.objects.all()
            unites = Unite.objects.all()
            
        if profile.id == 3:
            data = getPersonByMission(user)
            unites = getUniteByMission(user)    

        if profile.id == 2:
            data = getPersonByEquipe(user)
            unites = getUniteByEquipe(user)
    else:
        data = Person.objects.all()
        unites = Unite.objects.all()

    if request.method == "POST":
        rech = request.POST['rech']
        p = Paginator(
            data.filter(name__contains=rech) 
            | data.filter(firstname__contains=rech) 
            | data.filter(postname__contains=rech) 
            | data.filter(grade__contains=rech), 
            20
        )
        page = request.GET.get('page')
        pages = p.get_page(page)
    
    data_build = []
    for u in unites:
        p = Paginator(data.filter(unit=u.name), 20)
        page = request.GET.get('page')
        persons =p.get_page(page)
        npersons = data.filter(unit=u.name)
        data_build.append(
            {
                "unite":u,
                "persons":persons,
                "count":len(npersons)
            }
        )
       
    
    p = Paginator(data, 15)
    page = request.GET.get('page')
    pages = p.get_page(page)
        
    ctx = {
        'pages':pages,
        'compte': len(data),
        'unites' : data_build,
        'lpersone' : 'active',
        'data' : data
    }

    return render(request,'app_base/persons.html',ctx)

@login_required
def GarnisonView(request):
    garnisons = Garnison.objects.all()

    return render(request,'app_base/garnisons.html',{'garnisons':garnisons})

@login_required
def FingerprintView(request):
    fingerprints = Fingerprint.objects.all()

    return render(request,'app_base/fingerprints.html',{'fingerprints':fingerprints})

@login_required
def FaceView(request):
    faces = Face.objects.all()

    return render(request,'app_base/faces.html',{'faces':faces})

@login_required
def EquipeView(request):
    equipes = Equipe.objects.all()

    return render(request,'app_base/equipes.html',{'equipes':equipes})

@login_required
def ListedetailEquipe(request):
    detequipe = DetailEquipe.objects.all()

    return render(request,'app_base/detailEquipe.html',{'detail':detequipe})

@login_required
def ListeEquipe(request):
    equipes = Equipe.objects.all()
    ctx = {
        'equipes' : equipes,
    }
    return render(request,'app_base/ajouterEquipe.html', ctx)

@login_required
def addEquipe(request):
    if request.method == 'POST':
        fme = EquipeRegistraction(request.POST)
        if fme.is_valid():
           fme.save()
           return HttpResponseRedirect('/equipes')

    else:
        fme = EquipeRegistraction()
       
    return render(request,'app_base/ajouterEquipe.html',{'formeq':fme})

@login_required
def editEquipe(request, id):
    equipes = Equipe.objects.get(pk=id)
    form = EquipeRegistraction(request.POST or None,instance= equipes)
    if form.is_valid():
        form.save()
        return HttpResponseRedirect('/equipes')
    return render(request,'app_base/modifierEquipe.html',{'id':equipes,'form':form})

@login_required
def editControle(request, contr_id):
    conts = Controle.objects.get(pk=contr_id)
    form = ControleRegistraction(request.POST or None,instance= conts)
    if form.is_valid():
        form.save()
        return HttpResponseRedirect('/controles')
    return render(request,'app_base/modifierControle.html',{'id':conts,'formc':form})

@login_required
def deleteequipe(request, id):
    if request.method == 'POST':
        pi = Equipe.objects.get(pk=id)
        pi.delete()
        return HttpResponseRedirect('/equipes')
    else:
        return HttpResponseRedirect('/equipes') 

@login_required
def deletecontroles(request, id):
    if request.method == 'POST':
        pi = Controle.objects.get(pk=id)
        pi.delete()
        return HttpResponseRedirect('/controles')
    else:
        return HttpResponseRedirect('/controles') 

@login_required
def deletedocument(request, id):
    if request.method == 'POST':
        pi = Document.objects.get(pk=id)
        pi.delete()
        return HttpResponseRedirect('/listedocuments')
    else:
        return HttpResponseRedirect('/listedocuments') 

@login_required
def deletedetailequipe(request, id):
    if request.method == 'POST':
        pi = DetailEquipe.objects.get(pk=id)
        pi.delete()
        return HttpResponseRedirect('/ListedetailEquipes')
    else:
        return HttpResponseRedirect('/ListedetailEquipes') 

@login_required
def deleteusers(request, id):
    if request.method == 'POST':
        pi = User.objects.get(pk=id)
        pi.delete()
        return HttpResponseRedirect('/listeusers')
    else:
        return HttpResponseRedirect('/listeusers') 

@login_required
def listecontrole(request):
    contr = Controle.objects.all()

    return render(request,'app_base/controles.html',{'controles':contr})

@login_required
def listedocument(request):
    contr = Document.objects.all()

    return render(request,'app_base/documents.html',{'documents':contr})

@login_required
def detailunite(request):
    detaiu= DetailUnite.objects.all()

    return render(request,'app_base/detailunites.html',{'detailunit':detaiu})

@login_required
def listeusers(request):
    listuser= User.objects.all()

    return render(request,'app_base/listeusers.html',{'utilisateurs':listuser})

@login_required
def ajouterControle(request):
    if request.method == 'POST':
        fm = ControleRegistraction(request.POST)
        if fm.is_valid():
           fm.save()
           return HttpResponseRedirect('/controles')

    else:
        fm = ControleRegistraction()

    return render(request,'app_base/ajouterControle.html',{'forms':fm})

@login_required
def ajouterusers(request):
    if request.method == 'POST':
        fm = UserRegistraction(request.POST)
        if fm.is_valid():
           fm.save()
           return HttpResponseRedirect('/controles')

    else:
        fm = UserRegistraction()

    return render(request,'app_base/ajouterusers.html',{'forms':fm})

@login_required
def ajouterDetailunite(request):
    if request.method == 'POST':
        fm = DetailuniteRegistraction(request.POST)
        if fm.is_valid():
           fm.save()
           return HttpResponseRedirect('/detailunites')

    else:
        fm = DetailuniteRegistraction()

    return render(request,'app_base/ajouterdetailunite.html',{'forms':fm})

@login_required
def ajouterdetailequipe(request):
    if request.method == 'POST':
        fm = DetailequipeRegistraction(request.POST)
        if fm.is_valid():
           fm.save()
           return HttpResponseRedirect('/ListedetailEquipes')

    else:
        fm = DetailequipeRegistraction()

    return render(request,'app_base/ajouterdetailequipe.html',{'forms':fm})

@login_required
def ajouterdocument(request):
    if request.method == 'POST':
        fm = DocumentRegistraction(request.POST)
        if fm.is_valid():
           fm.save()
           return HttpResponseRedirect('/listedocuments')

    else:
        fm = DocumentRegistraction()

    return render(request,'app_base/ajouterdocument.html',{'forms':fm})

@login_required
def editdetailequipe(request, id):
    detail = DetailEquipe.objects.get(pk=id)
    form = DetailequipeRegistraction(request.POST or None,instance= detail)
    if form.is_valid():
        form.save()
        return HttpResponseRedirect('/ListedetailEquipes')
    return render(request,'app_base/modifierdetailEquipe.html',{'id':detail,'form':form})

@login_required
def editdocument(request, id):
    doc = Document.objects.get(pk=id)
    form = DocumentRegistraction(request.POST or None,instance= doc)
    if form.is_valid():
        form.save()
        return HttpResponseRedirect('/listedocuments')
    return render(request,'app_base/modifierdocument.html',{'id':doc,'formd':form})

@login_required
def rapport(request):
    return render(request,'app_base/rapportpdf.html')