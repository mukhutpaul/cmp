from django.db.models.signals import post_save
from django.dispatch import receiver
from app_controle.models import Controle

# @receiver(post_save, sender=Controle)
# def update_controle(sender, instance,created, **kwargs):
#     print("VVVVV ",instance.present)
#     if instance.present == True:
#         instance.situation = "normale"
#         instance.status = "ok"
#     elif instance.justifie == True:
#         instance.situation = "a_regulariser"
    
    