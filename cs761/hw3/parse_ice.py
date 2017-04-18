import csv
rows  = []
with open("ice.csv", newline='') as ice:
    reader = csv.reader(ice,delimiter=',')
    read_header = False
    for row in reader:
        if(not read_header):
            read_header = True
            continue
        year = int(row[0].split('-')[0])
        ice_days = int(row[1])
        rows.append((year,ice_days))

with open('ice_parsed.csv','w',newline='') as ice_parsed:
    writer = csv.writer(ice_parsed,delimiter=',')
    for row in rows:
        writer.writerow(row)

