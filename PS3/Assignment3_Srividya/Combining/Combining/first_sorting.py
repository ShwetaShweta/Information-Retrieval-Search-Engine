import operator
from collections import OrderedDict
import os

index = {}
i=1
base_path = '/home/ramyashenoy/PA3/' 
index_dir = 'indexFiles1/'



directory = 'sort/1/ag'
if not os.path.exists(base_path + directory):
	os.makedirs(directory)

directory = 'sort/1/hn'
if not os.path.exists(base_path + directory):
	os.makedirs(directory)

directory = 'sort/1/ou'
if not os.path.exists(base_path + directory):
	os.makedirs(directory)

directory = 'sort/1/vz'
if not os.path.exists(base_path + directory):
	os.makedirs(directory)

filelist = os.listdir(base_path + index_dir)

for filename in filelist:
	with open(base_path + index_dir + filename, 'r') as infile:
		for line in infile:
			token = line.split("#")[0]
			if token not in index:
				index[token] = line
			else:
				new = line.split("#")
				old = index[token].rstrip()
				old = old.split("#")
				updated_freq = int(old[1]) + int(new[1])
				updated_posting_list = old[2] + ',' + new[2]
				index[token] = token + "#" + str(updated_freq) + "#" + updated_posting_list 
		
		if i%15 == 0:
			print "Number of keys = " + str(len(index.keys()))
			print "Log: done constructing index"
			sorted_index = OrderedDict(sorted(index.iteritems(), key=operator.itemgetter(0)))
			print "Log: done sorting index"
			ag = open('sort/1/ag/ag' + str(i) +'.txt', 'w')
			hn = open('sort/1/hn/hn' + str(i) +'.txt', 'w')
			ou = open('sort/1/ou/ou' + str(i) +'.txt', 'w')
			vz = open('sort/1/vz/vz' + str(i) +'.txt', 'w')

			for key, value in sorted_index.iteritems():
				if ord(key[:1]) >= 97 and ord(key[:1]) <104:
					ag.write(value)

				elif ord(key[:1]) >= 104 and ord(key[:1]) < 111:
					hn.write(value)

				elif ord(key[:1]) >= 111 and ord(key[:1]) < 118:
					ou.write(value)

				elif ord(key[:1]) >= 118:
					vz.write(value)


			ag.close()
			hn.close()
			ou.close()
			vz.close()
			index={}
			print "Log: done writing files"

		i+=1	

print "Log: Writing remaining entries"

ag = open('sort/1/ag/ag' + str(i) +'.txt', 'w')
hn = open('sort/1/hn/hn' + str(i) +'.txt', 'w')
ou = open('sort/1/ou/ou' + str(i) +'.txt', 'w')
vz = open('sort/1/vz/vz' + str(i) +'.txt', 'w')

for key, value in sorted_index.iteritems():
			if ord(key[:1]) >= 97 and ord(key[:1]) <104:
				ag.write(value)

			elif ord(key[:1]) >= 104 and ord(key[:1]) < 111:
				hn.write(value)

			elif ord(key[:1]) >= 111 and ord(key[:1]) < 118:
				ou.write(value)

			elif ord(key[:1]) >= 118:
				vz.write(value)	

ag.close()
hn.close()
ou.close()
vz.close()

print "Log: completed"
	


		
	
