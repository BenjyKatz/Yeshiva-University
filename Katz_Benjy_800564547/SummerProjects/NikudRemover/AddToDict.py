#python AddToDict.py nikud.txt nonNikud.txt fileDestTag
import pandas as pd
import sys
import os
#change a word to remove all vavim and yudim
def getBase(word):
    finalWord = ""
    for c in word:
        if((c>"\u0590"and c<"\u05c8") or c == "ו" or c == "י"):
            finalWord = finalWord+""
        else:
            finalWord = finalWord+c
    return finalWord

#return the amount of vavim and yudim in the original word
def getVavsAndYuds(word):
    NumOfVavs = 0
    NumOfYuds = 0
    for c in word:
        if c == "ו":
            NumOfVavs+=1
        if c == "י":
            NumOfYuds+=1
    return NumOfVavs, NumOfYuds

chaserToMaleh = {'Chaser':'Maleh'}
nikudText = open(sys.argv[1])
nonNikudText = open(sys.argv[2])
fixedNikudText = ""
for line in nikudText:
    newLine = ""
    for c in line:
        if not(c > "\u0590" and c<"\u05F5" or c == "\"" or c == "\'"):#if it is not a letter, create a new word
            c = " "
        newLine = newLine+c
    fixedNikudText = fixedNikudText + newLine

#do the same thing for the non nikud text
fixedNonNikudText = ""
for line in nonNikudText:
    newLine = ""
    for c in line:
        if not(c > "\u0590" and c<"\u05F5" or c == "\""or c == "\'"):
            c = " "
        newLine = newLine+c
    fixedNonNikudText = fixedNonNikudText + newLine
basicNikud = ""
basicNonNikud = ""
for c in fixedNikudText:
    if((c>"\u0590"and c<"\u05c8") or c == "ו" or c == "י"):
        c = ""    
    basicNikud = basicNikud + c
for c in fixedNonNikudText:
    if((c>"\u0590"and c<"\u05c8") or c == "ו" or c == "י"):
        c = ""    
    basicNonNikud = basicNonNikud + c
basicNikudFile = open("basicNikudFile.txt", "w", encoding = 'iso-8859-8')
basicNonNikudFile = open("basicNonNikudFile.txt", "w", encoding = 'iso-8859-8')
basicNikudFile.write(basicNikud)
basicNonNikudFile.write(basicNonNikud)
basicNikudFile.close()
basicNonNikudFile.close()
#Utilize wdiff, a linux program to apply the longest common subsequence algorithm to find where the two files are the same 
#and where they are different, focus on the similarities and match the maleh to the chaser
#This likely only works when run on a linux machine
os.system('wdiff basicNikudFile.txt basicNonNikudFile.txt -1 -2 > similarities.txt')
similaritiesFile = open("similarities.txt", "r", encoding = 'iso-8859-8' )
nikudWords = fixedNikudText.split()
nonNikudWords = fixedNonNikudText.split()
for line in similaritiesFile:
    similarWords = line.split()
    for word in similarWords:
        counter = 0
        if(word.__contains__("=")):
            continue
        print(word)
        nikudWord = nikudWords.pop(0)
        #These while loops fix issues with slight girsa differences that would offset the entire dictionary
        while not(getBase(nikudWord) == word):
            counter+=1
            nikudWord = nikudWords.pop(0)
        print("nikud "+ nikudWord)
        nonNikudWord = nonNikudWords.pop(0)
        while not(getBase(nonNikudWord) == word):
            counter+=1
            nonNikudWord = nonNikudWords.pop(0)
        print("nonNikud "+ nonNikudWord)
        print("Counter: "+str(counter))
        print()
        NumOfVavsNikud, NumOfYudsNikud = getVavsAndYuds(nikudWord)
        NumOfVavsNonNikud, NumOfYudsNonNikud = getVavsAndYuds(nonNikudWord)
        if(NumOfVavsNonNikud>=NumOfVavsNikud and NumOfYudsNonNikud>= NumOfYudsNikud):
            chaserToMaleh[nikudWord] = nonNikudWord


similaritiesFile.close()
df = pd.DataFrame.from_dict(chaserToMaleh, orient="index")
df.to_csv("ChaserToMalehDicts/"+"chaserToMaleh"+sys.argv[3]+".csv", header = False)
print(list(chaserToMaleh.values())[0])