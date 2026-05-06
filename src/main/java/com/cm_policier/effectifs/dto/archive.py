from django.db import models
from app_base.models import Person
from app_controle.models.default import Seance
from app_controle.models.justification import Justification

from app_user.models import User

class Archive(models.Model):
    date_debut = models.DateField(blank=True, null=True)
    date_fin = models.DateField(blank=True, null=True)
    charge_mission = models.ForeignKey(User, on_delete=models.CASCADE)
    zone = models.CharField(max_length=250, null=True, blank=True)
    numero = models.CharField(max_length=50, blank=True, unique=True)

class ArchiveDetail(models.Model):
    archive = models.ForeignKey(Archive, on_delete=models.DO_NOTHING)
    id = models.BigIntegerField(primary_key=True)
    uid = models.CharField(max_length=40, unique=True, null=True)

    person = models.ForeignKey(Person, on_delete=models.DO_NOTHING, blank=True, null=True)
    noms = models.CharField(max_length=250, null=True, blank=True)
    base_donnee = models.BooleanField(default=False)
    liste_cmd = models.BooleanField(default=False)
    present = models.BooleanField(default=False)
    justifie = models.BooleanField(default=False)
    justification = models.ForeignKey(Justification, on_delete=models.DO_NOTHING, blank=True, null=True)
    observation = models.TextField(blank=True)
    controleur = models.ForeignKey(User, on_delete=models.DO_NOTHING, blank=True, null=True)
    is_controle = models.BooleanField(default=False)
    situation = models.CharField(max_length=30, default='pas_normale')
    status = models.CharField(max_length=20, default='pas_ok')

    seance = models.ForeignKey(Seance, on_delete=models.DO_NOTHING, null=True, blank=True)

    chef_equipe = models.ForeignKey(User, on_delete=models.DO_NOTHING, blank=True, null=True, related_name="archivechefequipe")
    charge_mission = models.ForeignKey(User, on_delete=models.DO_NOTHING, blank=True, null=True, related_name="archivechargemission")

    matricule = models.CharField(max_length=50,null=True, blank=True)
    unite = models.CharField(max_length=255,null=True, blank=True)

    grade = models.CharField(max_length=255,null=True, blank=True)
    sexe = models.CharField(max_length=1,null=True, blank=True)

    fingerprint4 = models.TextField(null=True, blank=True)
    fingerprint = models.TextField(null=True, blank=True)
    face = models.TextField(null=True, blank=True)

    is_cmd = models.BooleanField(default=False)

    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    is_actif = models.BooleanField(default=False)

    qrcode = models.ImageField(upload_to='qrcode/', null=True)

    id_controle = models.BigIntegerField(null=True)

    def __str__(self):
        name = self.noms or ""
        return name