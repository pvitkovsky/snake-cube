import sys

result = ''
for line in sys.stdin:
	for i in range(len(line.strip())-1):
		result += 'R'
	result += 'D'

print(result[:-1])


