import sys

for line in sys.stdin:
	for i in range(len(line.strip())-1):
		sys.stdout.write('R')
	sys.stdout.write('D')


