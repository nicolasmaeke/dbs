#csv einlesen und die Hashtags in der Liste hashtags speichern
import csv

portfolio = csv.reader(open("hashtag_network_Nodes.csv"),delimiter=',')
portfolio_list = []
portfolio_list.extend(portfolio)
hashtags = []
for data in portfolio_list:
    data = data[0].split(";")
    hashtags.append(data[1])
#print(hashtags)

def cutHash(x): #Funktion schneidet die Raute ab
    for i in range(len(x)):
        x[i] = x[i][1:]
    #print(x)

def isSubstring(x,y): #Funktion überprüft, ob ein Hashtag ein Substring eines anderen Hashtags ist und umgekehrt
    result = False
    value = x.find(y)
    value1 = y.find(x)
    if(value != -1 or value1 != -1):
        result = True
    return result

def similarity(x): #Funktion berechnet die Aehnlichkeit von zwei Hashtags
    kanten = []
    for i in range(len(x)):
        for j in range(len(x)):
            if x[i] != x[j]:
                similarity = 0
                if isSubstring(x[i],x[j]): #falls Substring, dann ist die Aehnlichkeit die Länge des Substring
                    numLetters = [len(x[i]),len(x[j])]
                    similarity = min(numLetters)
                    if similarity > 2:
                        if (str(j),str(i),similarity) not in kanten: #doppelte Kanten vermeiden
                            kanten.append((str(i),str(j),similarity))
                else: #falls kein Substring, dann ist die Aehnlichkeit die Anzahl gleicher Bigrams
                    bigrams1 = createBigrams(x[i])
                    bigrams2 = createBigrams(x[j])
                    bigrams = [len(bigrams1), len(bigrams2)]
                    index = min(bigrams)
                    for a in range(index):
                        b = 0
                        while b < index:
                            if bigrams1[a] == bigrams2[b]:
                                similarity += 1
                            b += 1
                    similarity = similarity*2 #zur besseren Skalierung wird hier die Aehnlichkeit verdoppelt
                    if similarity > 2:
                        if (str(j),str(i),similarity) not in kanten:
                            kanten.append((str(i),str(j),similarity))
    return kanten


def createBigrams(x):
    setOfBigrams = []
    m = 0
    n = 2
    while n <= len(x):
        setOfBigrams.append(x[m:n])  
        m += 2  
        n += 2 
    return setOfBigrams  
    

cutHash(hashtags)
data = (similarity(hashtags))      
print(data)

           
out = csv.writer(open("myfile.csv","w"), delimiter=',',quoting=csv.QUOTE_ALL)
for i in data:
    out.writerow(i)
