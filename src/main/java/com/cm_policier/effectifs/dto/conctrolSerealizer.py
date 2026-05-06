import base64
import binascii
from requests import Session
from rest_framework.serializers import ModelSerializer
from rest_framework import serializers

from app_base.models import Face, Fingerprint, Person, Unite
from drf_extra_fields.fields import Base64ImageField
from app_controle.models.archive import ArchiveDetail
from app_controle.models.controle import Controle, RetrieveList
from app_controle.models.default import Seance
from app_controle.models.justification import Justification
from app_controle.models.licence import Licence
from app_controle.models.mission import MissionUnite
from app_controle.models.document import Document
from app_user.models import LogUser

from app_user.serializers import EquipeSerializer, UserSerializer

class LicenceSerializer(ModelSerializer):

    class Meta:
        model = Licence
        fields = '__all__'

class MissionUniteSerializer(ModelSerializer):

    class Meta:
        model = MissionUnite
        fields = '__all__'

class UniteSerializer(ModelSerializer):
    controleur = serializers.SerializerMethodField('get_de')
    equipes = serializers.SerializerMethodField('get_equipes')
    missionunites = serializers.SerializerMethodField('get_missionunites')

    class Meta:
        model = Unite
        fields = '__all__'
    
    def get_de(self, obj):
        listes = []
        details = obj.detailunite_set.all()
        for d in details:
            listes.append(
                {
                    "user": UserSerializer(d.user).data,
                    "id_du":d.id
                }
            )
        return listes
    
    def get_equipes(self, obj):
        listes = []
        d_equipes = obj.equipeunite_set.all()
        for d in d_equipes:
            listes.append({
                "equipe": EquipeSerializer(d.equipe).data,
                "id_de":d.id
            })
        return listes
    
    def get_missionunites(self, obj):
        mus = MissionUnite.objects.filter(unite_id=obj.id)
        mu = None
        if mus.exists():
            mu = mus.first()
        data = MissionUniteSerializer(mu).data
        return data
            

class BinaryField(serializers.Field):
    def to_representation(self, value):
        from base64 import b64encode
        return b64encode(value).decode('utf-8')
        #return binascii.hexlify(value).decode('utf-8')

    def to_internal_value(self, value):
        from base64 import b64decode
        return b64decode(value)

class FingerprintSerializer(ModelSerializer):

    class Meta:
        model = Fingerprint
        fields = '__all__'

class FaceSerializer(ModelSerializer):

    class Meta:
        model = Face
        fields = '__all__'

class PersonSerializer(ModelSerializer):
    finger = serializers.SerializerMethodField('get_finger')
    face = serializers.SerializerMethodField('get_face')

    class Meta:
        model = Person
        fields = ['uuid','sex','name','firstname','postname','province','unit','currentnrnew','status','grade','finger','face','birthdate','birthplace','bloodtype']
    
    def get_finger(self, obj):
        finger = Fingerprint.objects.filter(uuid=obj)
        return FingerprintSerializer(finger, many=True).data
    
    def get_face(self, obj):
        face = Face.objects.filter(uuid=obj)
        return FaceSerializer(face, many=True).data

class JustificationSerializer(ModelSerializer):

    class Meta:
        model = Justification
        fields = '__all__'

class DocumentSerializer(ModelSerializer):
    image_url = Base64ImageField(max_length=None, use_url=True,) 

    class Meta:
        model = Document
        fields = '__all__'

class SeanceSerializer(ModelSerializer):

    class Meta:
        model = Seance
        fields = '__all__'

class ArhiveDetailSerializer(ModelSerializer):
    class Meta:
        model = ArchiveDetail
        fields = '__all__'

class ControleSerializer(ModelSerializer):
    documents = serializers.SerializerMethodField('get_documents')
    qrcode = Base64ImageField(max_length=None, use_url=True, required=False)
    seance = serializers.SerializerMethodField('get_seance')

    class Meta:
        model = Controle
        exclude = ('fingerprint4', )
        #fields = '__all__'
    
    def get_documents(self, obj):
        docs = obj.document_set.all()
        data = DocumentSerializer(docs, many=True).data
        return data
    
    def get_seance(self, obj):
        id_seance = obj.seance_id
        if id_seance:
            data = SeanceSerializer(Seance.objects.get(pk=id_seance)).data
        else:
            data = None
        return data

class ControleSerializerNotFinger(ModelSerializer):
    documents = serializers.SerializerMethodField('get_documents')
    qrcode = Base64ImageField(max_length=None, use_url=True, required=False)
    seance = serializers.SerializerMethodField('get_seance')

    class Meta:
        model = Controle
        exclude = ('fingerprint4', 'fingerprint')
        #fields = '__all__'
    
    def get_documents(self, obj):
        docs = obj.document_set.all()
        data = DocumentSerializer(docs, many=True).data
        return data
    
    def get_seance(self, obj):
        seance = obj.seance
        data = SeanceSerializer(seance).data
        return data

class ListeCmdSerializer(ModelSerializer):

    class Meta:
        model = RetrieveList
        fields = '__all__'

class SessionSerializer(ModelSerializer):

    class Meta:
        model = Session
        fields = '__all__'

class LoguserSerializer(ModelSerializer):

    class Meta:
        model = LogUser
        fields = '__all__'