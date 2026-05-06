from django.shortcuts import redirect, render
from django.core.paginator import Paginator
from django.contrib.auth.decorators import login_required
from app_controle.utils import uniteByUser
from app_user.models import User
from app_base.models import Person, Unite
from root.utils import effectif, get_next_or_prev


@login_required
def personUnite(request):
    myid = request.POST.get('id')
    unite = Unite.objects.get(pk = myid)

    ctr = ""
    detailunites = unite.detailunite_set.all()
    for index, du in enumerate(detailunites) :
        if index == 0 :
            ctr = du.user.noms
        else :
            ctr += " / " + du.user.noms 
    
    equipe = ""
    equipeunites = unite.equipeunite_set.all()
    for index, eu in enumerate(equipeunites) :
        if index == 0 :
            equipe = eu.equipe.user.noms[2:]
        else :
            equipe += " / " + eu.equipe.user.noms[2:]

    mission = ""
    missionunites = unite.missionunite_set.all()
    for index, mu in enumerate(missionunites) :
        if index == 0 :
            mission = mu.mission.charge_mission.noms[2:]
        else :
            mission += " / " + mu.mission.charge_mission.noms[2:] 

    affectations = {
        "ctr" : ctr,
        "equipe" : equipe,
        "mission" : mission
    }
    u = unite.name
    data = Person.objects.filter(unit = u).order_by('name')
    p = Paginator(data, 20)
    page = request.GET.get('page')
    pages =p.get_page(page)

    if request.method == "POST" :
        rech = request.POST['rech']
        data = data.filter(name__icontains=rech) | data.filter(firstname__icontains=rech) | data.filter(postname__icontains=rech)
        p = Paginator(data, 20)
        page = request.GET.get('page')
        pages = p.get_page(page)
    
    unites = uniteByUser(request.user.id)

    ids = list()
    ids = request.POST.getlist('ids[]')
    n = len(ids)
    id_prev = id_next = index_tag = -1
    for index, id in enumerate(ids):
        if int(id) == int(myid):
            print("IDX TAG", index)
            index_tag = index
            if index > 0 :
                id_prev = index - 1
            if index <  n-1:
                id_next = index + 1

    nav = {
        "next" : -1,
        "prev" : -1
    }
    if id_next != -1 : nav['next'] = int(ids[id_next])
    if id_prev != -1 : nav['prev'] = int(ids[id_prev])

    ctx = {
        'person_unite': pages,
        'compte' : len(data),
        'unite':unite,
        'e': effectif(unite),
        'affectations': affectations,
        'nav':nav,
        'data':ids
    }
    return render(request,'app_controle/personUnite.html',ctx)

def next_unite(request, myid):
    unite = Unite.objects.get(pk=myid)
    id_user = request.user.id
    user = User.objects.get(pk=id_user)

    profile = 0
    if user.profile :
        profile = user.profile.id
    
    if profile == 0:
        unites = Unite.objects.order_by('id')
    else:
        unites = uniteByUser(id_user)
    
    tag = get_next_or_prev(unites,unite,'next')

    return redirect('personUnite', myid=int(tag.id))

def prev_unite(request, myid):
    unite = Unite.objects.get(pk=myid)
    id_user = request.user.id
    user = User.objects.get(pk=id_user)

    profile = 0
    if user.profile :
        profile = user.profile.id
    
    if profile == 0:
        unites = Unite.objects.order_by('id')
    else:
        unites = uniteByUser(id_user)
    
    tag = get_next_or_prev(unites,unite,'prev')

    return redirect('personUnite', myid=int(tag.id))