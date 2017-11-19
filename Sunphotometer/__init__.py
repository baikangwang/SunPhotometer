import sys
import os
sppath = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'SunPhotometer.jar')
urarpath = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'junrar-0.7.jar')
sys.path.append(sppath)
sys.path.append(urarpath)