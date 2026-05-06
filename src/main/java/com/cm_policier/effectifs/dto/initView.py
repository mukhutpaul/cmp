import json
from rest_framework.viewsets import ModelViewSet
from rest_framework.permissions import IsAuthenticated
from app_controle.models import Licence, Tablette, TabletteLicence
from app_controle.serializers import LicenceSerializer
from app_user.models import User
from rest_framework.views import APIView
from django.http import JsonResponse
from rest_framework.response import Response
from rest_framework import status

from app_user.serializers import UserSerializer


class InitViewSet(ModelViewSet):
    serializer_class = UserSerializer
    queryset = User.objects.filter(profile_id=1)
    #permission_classes = [IsAuthenticated]

class SendUserViewSet(ModelViewSet):
    serializer_class = UserSerializer
    queryset = User.objects.exclude(username="admin").order_by('id')

class LicenceView(APIView):

    def post(self, request):
        data = json.loads(request.body)

        sn = data.get("sn", None)

        if sn is None:
            rdata = {
                "detail":"Aucun numero de serie trouvé"
            }
            return Response(status=status.HTTP_404_NOT_FOUND, data=rdata)
        else:
            rtablette = Tablette.objects.filter(serial_number=sn)
            if rtablette.exists():
                tablette = rtablette.first()
                rtl = TabletteLicence.objects.filter(tablette=tablette)
                if rtl.exists():
                    data = []
                    for rt in rtl:
                        license = rt.license
                        data.append(
                            {
                                "nom":license.type_licence,
                                "data":license.data
                            }
                        )
                    rdata = {
                        "detail":data
                    }
                    return Response(status=status.HTTP_200_OK, data=rdata)
                else:
                    rdata = {
                        "detail":"Aucune Licence trouvée"
                    }
                    return Response(status=status.HTTP_404_NOT_FOUND, data=rdata)    
            else:
                t = Tablette()
                t.serial_number = sn
                t.model_tablette = "COPPERNIC"
                t.save()
                rdata = {
                        "detail":"Tablette ajoutée"
                    }
                return Response(status=status.HTTP_404_NOT_FOUND, data=rdata)