from django.conf.urls.static import static
from django.conf import settings
from django.urls import path, include
from rest_framework_simplejwt.views import TokenRefreshView, TokenVerifyView
from rest_framework import routers
from app_controle.views.controle_views import ControleViewSet, DocumentViewSet, FaceViewSet, FingerViewSet, GetListControle, JustificationViewSet, ListeCmdView, PersonViewSet, ResultatView, sDocumentViewSet, sEquipeViewSet, sMissionViewSet, sUniteViewSet
from app_controle.views.init_views import InitViewSet, LicenceView, SendUserViewSet
from app_controle.views.militaire_views import MilitaireView
from app_controle.views.person_views import DataPerson, PersonAPIView

from app_controle.views.unite_views import UniteAPIViewset
from app_user.views import ApiToken, EquipeAPIViewset, MissionAPIViewset, MyTokenObtainPairView, RegisterViewset, UserAPIViewset, login_remote

from app_controle.admin import controle_site
from app_controle.views.synchronization_views import LoguserView, SControleView, config, import_data, init_data, on_synchronization, start_synchronization, synchronize_data, writeIP

router = routers.SimpleRouter()

router.register('unites', UniteAPIViewset, basename='unites')
router.register('equipes', EquipeAPIViewset, basename='equipes')
router.register('missions', MissionAPIViewset, basename='missions')

router.register('fingers', FingerViewSet, basename='fingers')
router.register('faces', FaceViewSet, basename='faces')

router.register('spersons', PersonViewSet, basename='spersons')
router.register('sunites', sUniteViewSet, basename='sunites')
router.register('sdocuments', sDocumentViewSet, basename='sdocuments')
router.register('smissions', sMissionViewSet, basename='smissions')
router.register('sequipes', sEquipeViewSet, basename='sequipes')

router.register('user', UserAPIViewset, basename='user')
router.register('register', RegisterViewset, basename='register')
router.register('controle', ControleViewSet, basename='controle')
router.register('documents', DocumentViewSet, basename='documents')
router.register('justifications', JustificationViewSet, basename='justifications')
router.register('initusers', InitViewSet, basename='initusers')
router.register('sendusers', SendUserViewSet, basename='sendusers')

urlpatterns = [
    path('admin/', controle_site.urls),
    path('', include('app_base.urls')),
    path('controle/', include('app_controle.urls')),
    path('report/', include('app_report.urls')),
    path('user/', include('app_user.urls')),
    path('api-auth/', include('rest_framework.urls')),
    path('api/token/', MyTokenObtainPairView.as_view(), name='obtain_token'),
    path('api/token/refresh/', TokenRefreshView.as_view(), name='refresh_token'),
    path('api/token/verify/', TokenVerifyView.as_view(), name='token_verify'),
    
    path('api/controles/', ResultatView.as_view(), name='controles'),

    path('api/scontrole/', SControleView.as_view(), name='scontrole'),
    path('api/logusers/', LoguserView.as_view(), name='api-logusers'),
    

    path('apis/token/', ApiToken.as_view(), name='obtain_tokens'),

    path('api/licence/', LicenceView.as_view(), name='api_licence'),

    path('api/listecmd/', ListeCmdView.as_view(), name='api_listecmd'),
    path('api/listecontrole/', GetListControle.as_view(), name='api_listecontrole'),
    path('api/', include(router.urls)),

    path('config/', config, name='config'),
    path('writeip/', writeIP, name='writeip'),

    path('api/persons/', PersonAPIView.as_view()),
    path('api/datapersons/<int:id>', DataPerson.as_view()),

    path('login-remote/', login_remote, name='login-remote'),
    path('import_data/', import_data, name='import_data'),
    path('init_data/', init_data, name='init_data'),
    path('synchronize_data/', synchronize_data, name='synchronize_data'),

    path('start_synchronize/<int:liv>', start_synchronization, name='start_synchronization'),
    path('synchronize/', on_synchronization
         , name='synchronization'),

    path('api/militaires/', MilitaireView.as_view(), name='api_militaires')
] 

urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)