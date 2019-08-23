from django.db import models

# Create your models here.
class User(models.Model):
    id = models.AutoField(primary_key=True)
    username = models.CharField(db_index=True, max_length=100, unique=True)
    password = models.CharField(max_length=100)
    email = models.EmailField()
    register_date = models.DateTimeField(auto_now_add=True)

class JsonData(models.Model):
    user_id = models.ForeignKey(User, on_delete=models.CASCADE)
    varname = models.CharField(db_index=True, max_length=100, unique=True)
    json = models.TextField()
