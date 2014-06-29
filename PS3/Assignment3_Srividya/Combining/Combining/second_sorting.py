import operator
from collections import OrderedDict
import os

index = {}
base_path = '/home/ramyashenoy/PA3/sort/1/' 

directory = '2/'
if not os.path.exists(base_path + directory):
	os.makedirs(base_path + directory)

dirlist = ['ag', 'hn', 'ou', 'vz']

for d in dirlist:
	filelist = os.listdir(base_path + d)
	print "Files to read: "
	print filelist
	for filename in filelist:
		print "Reading " + filename
		with open(base_path + d + "/" + filename, 'r') as infile:
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
		
		
	print "number of keys = " + str(len(index.keys()))
	print "Log: Constructed index for " + d
	sorted_index = OrderedDict(sorted(index.iteritems(), key=operator.itemgetter(0)))
	print "Log: Sorted index for " + d
	f = open(base_path + directory + d +'.txt', 'w')
			
	for key, value in sorted_index.iteritems():
		f.write(value)

	f.close()
	print "Log: Completed writing files for " + d
	index = {}
	
