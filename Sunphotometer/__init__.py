import sys
import os

base=os.path.dirname(os.path.abspath(__file__))
libpath=os.path.join(base,"lib")
sys.path.append(libpath)
sys.path.append(os.path.join(libpath,"Lib"))
sys.path.append(os.path.join(libpath,"jython-standalone-2.7.1.jar","Lib"))
sys.path.append(os.path.join(base,"pylib"))
sys.path.append(os.path.join(base,"pylib","mipylib"))
sys.path.append(os.path.join(base,"MeteoInfoLib.jar"))
sys.path.append(os.path.join(base,"MeteoInfoLab.jar"))
sppath = os.path.join(base, 'SunPhotometer.jar')
urarpath = os.path.join(base, 'junrar-0.7.jar')
sys.path.append(sppath)
sys.path.append(urarpath)

# load all lib
jars=[os.path.join(libpath,f) for f in os.listdir(libpath) if os.path.isfile(os.path.join(libpath,f)) and f.endswith(".jar")]
for jar in jars:
    sys.path.append(jar)