from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from api import models
from django.http import HttpResponse, JsonResponse, HttpResponseForbidden
import json
from functools import wraps

def check_post(f):
    @wraps(f)
    def inner(req, *arg, **kwargs):
        if(req.method == 'POST'):
            return f(req, *arg, **kwargs)
        else:
            return HttpResponseForbidden()
    return inner

# Create your views here.
@csrf_exempt
@check_post
def add_user(req):
    result = {'status': -1, 'message': 'invalid_request' }
    try:
        data = json.loads(req.body)
        payload = data['payload']
        user, created = models.User.objects.get_or_create(username=payload['username'], defaults=payload)
        if(not created):
            result['message'] = 'user_already_exist'
            result['status'] = 1
        else:
            result['message'] = ''
            result['status'] = 0
    except Exception as e:
        print(e)
    finally:
        return JsonResponse(result)

@csrf_exempt
@check_post
def check_user(req):
    result = {'status': -1, 'message': 'invalid_request' }
    try:
        data = json.loads(req.body)
        payload = data['payload']
        user = models.User.objects.get(username=payload['username'])
        if(user.password == payload['password']):
            result = {'status': 0, 'message': ''}
        else:
            result = {'status': 1, 'message': 'invalid_username_or_password' }
    except models.User.DoesNotExist:
        result = {'status': 1, 'message': 'invalid_username_or_password' }
    except Exception as e:
        print(e)
    finally:
        return JsonResponse(result)

@csrf_exempt
@check_post
def get_user(req):
    result = {'status': -1, 'message': 'invalid_request' }
    try:
        data = json.loads(req.body)
        payload = data['payload']
        user = models.User.objects.get(username=payload['username'])
        if(user.password == payload['password']):
            result = {'status': 0, 'message': '', 'payload': {'email': user.email, 'register_date': user.register_date}}
        else:
            result = {'status': 1, 'message': 'invalid_username_or_password' }
    except models.User.DoesNotExist:
        result = {'status': 1, 'message': 'invalid_username_or_password' }
    except Exception as e:
        print(e)
    finally:
        return JsonResponse(result)

@csrf_exempt
@check_post
def update_user(req):
    result = {'status': -1, 'message': 'invalid_request' }
    try:
        data = json.loads(req.body)
        payload = data['payload']
        user = models.User.objects.get(username=payload['username'])
        if(user.password == payload['password']):
            user.password = payload['new_password']
            user.email = payload['email']
            user.save()
            result = {'status': 0, 'message': ''}
        else:
            result = {'status': 1, 'message': 'invalid_username_or_password' }
    except models.User.DoesNotExist:
        result = {'status': 1, 'message': 'invalid_username_or_password' }
    except Exception as e:
        print(e)
    finally:
        return JsonResponse(result)

@csrf_exempt
@check_post
def del_user(req):
    result = {'status': -1, 'message': 'invalid_request' }
    try:
        data = json.loads(req.body)
        payload = data['payload']
        user = models.User.objects.get(username=payload['username'])
        if(user.password == payload['password']):
            result = {'status': 0, 'message': ''}
            user.delete()
        else:
            result = {'status': 1, 'message': 'invalid_username_or_password' }
    except models.User.DoesNotExist:
        result = {'status': 1, 'message': 'invalid_username_or_password' }
    except Exception as e:
        print(e)
    finally:
        return JsonResponse(result)

@csrf_exempt
@check_post
def put_json(req):
    result = {'status': -1, 'message': 'invalid_request' }
    try:
        data = json.loads(req.body)
        payload = data['payload']
        user = models.User.objects.get(username=payload['username'])
        if(user.password == payload['password']):
            models.JsonData.objects.update_or_create(user_id=user, varname=payload['varname'], defaults={'varname': payload['varname'],
            'json': payload['json'], 'user_id': user})
            result = {'status': 0, 'message': ''}
        else:
            result = {'status': 1, 'message': 'invalid_username_or_password' }
    except models.User.DoesNotExist:
        result = {'status': 1, 'message': 'invalid_username_or_password' }
    except Exception as e:
        print(e)
    finally:
        return JsonResponse(result)

@csrf_exempt
@check_post
def del_json(req):
    result = {'status': -1, 'message': 'invalid_request' }
    try:
        data = json.loads(req.body)
        payload = data['payload']
        user = models.User.objects.get(username=payload['username'])
        if(user.password == payload['password']):
            models.JsonData.objects.filter(user_id=user, varname=payload['varname']).delete()
            result = {'status': 0, 'message': ''}
        else:
            result = {'status': 1, 'message': 'invalid_username_or_password' }
    except models.User.DoesNotExist:
        result = {'status': 1, 'message': 'invalid_username_or_password' }
    except Exception as e:
        print(e)
    finally:
        return JsonResponse(result)


@csrf_exempt
@check_post
def get_json(req):
    result = {'status': -1, 'message': 'invalid_request' }
    try:
        data = json.loads(req.body)
        payload = data['payload']
        user = models.User.objects.get(username=payload['username'])
        if(user.password == payload['password']):
            result = {'status': 0, 'message': '', 'payload': {'json': ''}}
            json_get = models.JsonData.objects.get(user_id=user, varname=payload['varname'])
            result['payload']['json'] = json_get.json
        else:
            result = {'status': 1, 'message': 'invalid_username_or_password' }
    except models.JsonData.DoesNotExist:
        pass
    except models.User.DoesNotExist:
        result = {'status': 1, 'message': 'invalid_username_or_password' }
    except Exception as e:
        print(e)
    finally:
        return JsonResponse(result)