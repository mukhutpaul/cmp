import json
import profile
from rest_framework.response import Response
from app_base.models import Person, Unite
from app_controle.models import Controle
from app_controle.serializers import PersonSerializer
from rest_framework.views import APIView

from rest_framework.permissions import IsAuthenticated
from rest_framework import status


from app_controle.utils import getPersonByEquipe, getPersonByMission

from app_user.models import LogUser, Profile, User

class DataPerson(APIView):
    serializer_class = PersonSerializer
    permission_classes = [IsAuthenticated]

    def get(self, request, id):
        user = User.objects.get(pk=request.user.id)
        id_unite = id
        unite = Unite.objects.get(pk=id_unite)
        persons = Person.objects.filter(unit=unite.name)

        serializer = PersonSerializer(persons, many=True)
        if serializer.data is not None:
            count = len(serializer.data)
        else:
            count = 0
        
        return Response(data={"detail":serializer.data, "count":count} ,status=status.HTTP_200_OK)


class PersonAPIView(APIView):
    serializer_class = PersonSerializer
    permission_classes = [IsAuthenticated]

    def post(self, request):
        user = User.objects.get(pk=request.user.id)
        data = json.loads(request.body)
        r = {}
        try:
            profile = user.profile
        except Exception:
            profile = None
        
        if profile == 1 or profile == 4:
            r['msg']=f"Vous n'estes pas autorisé"
            r['status']=status.HTTP_401_UNAUTHORIZED
            return Response(data=r ,status=status.HTTP_401_UNAUTHORIZED)  
        n = 0
        
        controles = data
        
        for controle in controles:
            rs = Controle.objects.filter(matricule=controle.get('matricule'))
            e_controle = rs.exists()
            if not e_controle:
                Controle(**controle).save()
                n = n + 1
            else:
                c = rs.first()
                if c.justifie :
                    rs.update(**controle)
                    n = n + 1    
        LogUser(user=user,action=f"{n} donées synchronisées").save()
        r['msg']=f"{n} donées synchronisées"
        r['status']=status.HTTP_200_OK
        return Response(data=r,status=status.HTTP_200_OK)


    def get(self, request):
        id_user = request.user.id
        user = User.objects.get(pk=id_user)
        queryset = []

        try:
            profile = user.profile.id
        except Profile.DoesNotExist:
            profile = None
        
        if profile == 1 :
            queryset = []
        if profile == 2 :
            queryset = getPersonByEquipe(user)
        if profile == 3 :
            queryset = getPersonByMission(user)
        if user.is_superuser:
            queryset = Person.objects.all()
        
        serializer = PersonSerializer(queryset, many=True)
        if serializer.data is not None:
            count = len(serializer.data)
        else:
            count = 0
        
        return Response(data={"detail":serializer.data, "count":count} ,status=status.HTTP_200_OK)  