from rest_framework import serializers
from drf_extra_fields.fields import Base64ImageField
from app_controle.models.tablette import Tablette
from app_controle.models import default
from .models import User

class TabletteSerializer(serializers.ModelSerializer):

    class Meta:
        model = Tablette
        fields = '__all__'

class UserSerializer(serializers.ModelSerializer):
    photo_url = Base64ImageField(max_length=None, use_url=True,) 

    class Meta:
        model = User
        fields = ['id','email','password','username','finger_print', 'noms', 'photo_url', 'profile']
        #fields = '__all__'
    
    def create(self, validated_data):
        print("USER", validated_data)
        password = validated_data.pop('password', None)
        instance = self.Meta.model(**validated_data)
        if password is not None:
            instance.set_password(password)
        instance.is_active = False
        instance.profile_id = 1
        instance.save()
        return instance
    
    def update(self, instance, validated_data):
        for attr, value in validated_data.items():
            if attr == 'password':
                instance.set_password(value)
            else:
                setattr(instance, attr, value)
        instance.profile_id = 1
        instance.save()
        return instance

class EquipeSerializer(serializers.ModelSerializer):
    controleur = serializers.SerializerMethodField('get_de')

    class Meta:
        model = default.Equipe
        fields = '__all__'
    
    def get_de(self, obj):
        listes = []
        details = obj.detailequipe_set.all()
        for d in details:
            listes.append(
                {
                    'user': UserSerializer(d.user).data ,
                    'id':d.id
                }
            )
        return listes

class MissionSerializer(serializers.ModelSerializer):
    
    cm = serializers.SerializerMethodField('get_cm')

    class Meta:
        model = default.Mission
        fields = '__all__'
    
    def get_cm(self, obj):
        cm = User.objects.get(pk=obj.charge_mission.id)
        r = {
            'id' : cm.id,
            'email' : cm.email,
            'username' : cm.username,
            'profile_id' : cm.profile.id,
            'is_active' : cm.is_active,
            'password' : cm.password,
            'noms' : cm.noms
        }
        return r