# Author: Benjamin Katz 
# Phone: +12017448809(whatsapp)
# Email: benjykatz12@gmail.com
# Written Summer 2022

# This program takes a document with nikud and outputs a document with the nikud removed.
# The outputed ducument adds appropreiate vavim and yudim to change the chaser to maleh.
# The algorithm used to generate this output checks a large dictionary for every chaser word and replaces it with its maleh counterpart.
# In the event that a word is not in the dictionary, the algorithm looks for all posibilities of adding and not adding the yudim and vavim
# and sees which is most likely. This part of the algorithm works with the lowest level of accuracy so it is best to minimize dictionary misses.

#How to run:
#python NR.py allRambam/NikudRambamTest/NikudRambamTest.txt
#wdiff NikudDeleted.txt RambamNoNikud/NoNikudRambamTest/NoNikudRambamTest.txt -s -3
import pandas as pd
import itertools
import sys
import pickle
#Unicode charecter
kubutz = "\u05bb"
tsere = "\u05b5"
kamatz = "\u05b8"
chalom = "\u05b9"
chatefKamatz = "\u05b3"
chirik = "\u05b4"

#Inputs a single word and outputs every possibility of adding vav and yud given the nikud
def getWordRepArray(word):
    returnArray = []
    index = 0
    prevLetterVav = False
    isFirstNikudisChatefKamatz = False
    if(len(word)>1 and word[1] == chatefKamatz):
        isFirstNikudisChatefKamatz = True
    for c in word:
        if not(c == chirik or c == chalom or c == kubutz or c == tsere or c == kamatz):
            returnArray.insert(index, "")
            if c == "ו":
                prevLetterVav = True
                index+=1
                continue
        elif (c == chirik or c == tsere):#is chirik
            index-=1
            returnArray.pop(index)
            returnArray.insert(index, "י")
        elif ((c == chalom and not prevLetterVav) or c == kubutz or c == kamatz or (c == chatefKamatz and isFirstNikudisChatefKamatz)):#is halom
            isFirstNikudisChatefKamatz = False
            index-=1
            returnArray.pop(index)
            returnArray.insert(index, "ו")
        elif((c == chalom and  prevLetterVav)):
            returnArray.insert(index, "")
        index+=1
        prevLetterVav = False
        isFirstLetter = False
    
    return returnArray
def insert(subword, word, index):
    finalWord=""
    firstSubString = word[:index]
    secondSubString = word[index:]
    finalWord = firstSubString+subword+secondSubString
    return finalWord
#checks to see how common a word is in all given dictionarys
def searchAllDicts(word, allDicts):
    total = 0
    for mydict in allDicts:
        if not(mydict.get(word) == None):
           total = total +  mydict.get(word)
    return total
def checkMalehToChaser(word, CMDict):
    return CMDict.get(word)
    
def findBestWord(possibilities, allDicts):

    max = 0
    bestWord =""
    longestWord = ""
    for word in possibilities:
        total = searchAllDicts(word, allDicts)
        if total>max:
            max = total
            bestWord = word
        if len(word)>len(longestWord):
            longestWord = word
    return bestWord
    return longestWord

#Add all chaser to maleh dictionaries
#For speed up, utilize pickling correctly. Ensure that the pickle file is present and if not, create the pickle file

CMRambamDF = pd.read_csv("ChaserToMalehDicts/chaserToMalehRambam.csv", skipinitialspace=True)#encoding = 'iso-8859-8', 
CMRambamDict = dict(zip(CMRambamDF.Chaser, CMRambamDF.Maleh))
#print("Rambam: "+str(len(CMRambamDict)))
CMGemaraDF = pd.read_csv("ChaserToMalehDicts/chaserToMalehGemara.csv", skipinitialspace=True)
CMGemaraDict = dict(zip(CMGemaraDF.Chaser, CMGemaraDF.Maleh))
#print("Gemara: "+str(len(CMGemaraDict)))
CMToDoDF = pd.read_csv("ChaserToMalehDicts/chaserToMalehToDo.csv", skipinitialspace=True)#encoding = 'iso-8859-8', 
CMToDoDict = dict(zip(CMToDoDF.Chaser, CMToDoDF.Maleh))
#print("ToDo: "+str(len(CMToDoDict)))
#CMRambamDict.update(CMGemaraDict)
CMKSADF = pd.read_csv("ChaserToMalehDicts/chaserToMalehKSA.csv", skipinitialspace=True)#encoding = 'iso-8859-8', 
CMKSADict = dict(zip(CMKSADF.Chaser, CMKSADF.Maleh))
#print("KSA: "+str(len(CMKSADict)))

CMGemaraDict.update(CMRambamDict)
CMGemaraDict.update(CMToDoDict)
CMGemaraDict.update(CMKSADict)
CMRambamDict = CMGemaraDict
#print("NewDict: "+str(len(CMRambamDict)))
#tdf = pd.DataFrame.from_dict(CMRambamDict, orient="index")
#tdf.to_csv("ChaserToMalehDicts/"+"chaserToMaleh"+"Full"+".csv", header = False)

# rambamDataFrame = pd.read_csv("Milon/Rambam.csv", encoding = 'iso-8859-8', skipinitialspace=True);
# rishonimDataFrame = pd.read_csv("Milon/Rishonim.csv", encoding = 'iso-8859-8', skipinitialspace=True);
# parshanutDataFrame = pd.read_csv("Milon/ParshanutSA.csv", encoding = 'iso-8859-8', skipinitialspace=True);
# shasAchronimDataFrame = pd.read_csv("Milon/ShasAchronim.csv", encoding = 'iso-8859-8', skipinitialspace=True);
# shutAchronimDataFrame = pd.read_csv("Milon/ShutAchronim.csv", encoding = 'iso-8859-8', skipinitialspace=True);

# rambamDict = dict(zip(rambamDataFrame.word, rambamDataFrame.occurrences));
# rishonimDict = dict(zip(rishonimDataFrame.word, rishonimDataFrame.occurrences));
# parshanutDict = dict(zip(parshanutDataFrame.word, parshanutDataFrame.occurrences));
# shasAchronimDict = dict(zip(shasAchronimDataFrame.word, shasAchronimDataFrame.occurrences));
# shutAchronimDict = dict(zip(shutAchronimDataFrame.word, shutAchronimDataFrame.occurrences));
# allDicts = [rambamDict, rishonimDict, parshanutDict, shasAchronimDict, shutAchronimDict]

# pickle_out = open("listOfDicts.pickle", "wb")
# pickle.dump(allDicts, pickle_out)
# pickle_out.close()
if len(sys.argv) < 2:
    print("Please re-run the program with the file as the first argument")
    exit()

pickle_in = open("listOfDicts.pickle", "rb")
allDicts = pickle.load(pickle_in)





nikudDeletedFile = open("NikudDeleted.txt", "w")
with open(sys.argv[1]) as input:
    for line in input:
        replacedWithDictLine = ""
        dictPunctuation = []
        dictPunctuation.append("")
        for c in line:
            if not(c>="\u0590" and c<="\u05F4"):#if not nikkud or letter replace with a "#"
                replacedWithDictLine = replacedWithDictLine+"#"
                dictPunctuation.append(c)
            else:
                replacedWithDictLine = replacedWithDictLine+c
            
        withReplacment = ""
        #split up around every word
        for word in replacedWithDictLine.split("#"):
            if not(checkMalehToChaser(word, CMRambamDict)==None):
                withReplacment = withReplacment+"#"+checkMalehToChaser(word, CMRambamDict)
            else:
                withReplacment = withReplacment+"#"+word
        if(withReplacment[len(withReplacment)-1] =="#"):
            withReplacment =  withReplacment[:len(withReplacment)-1]

        fullLine = ""
        punctuation = []
        punctuation.append(" ")
        prevC = ""

        for c in withReplacment:
            if(c>="\u05D0" and c<="\u05F4" or c=="\u05B4" or c=="\u05B9" or c=="\u05BA" or c=="\u05C4" or c=="\u05C5" or c == kubutz or c == tsere or c == kamatz or c == chatefKamatz):#if it is a letter or a relevant nikud
                    fullLine = fullLine+c
            elif not(c>="\u0590" and c<="\u05CF"):#if not nikkud replace with a space
                fullLine = fullLine+"#"
                punctuation.append(c)
            else:#delete remaining nikkud
                fullLine = fullLine+""
            prevC = c
        punctuation = dictPunctuation
        fullChangedLine = ""
        begining = 0
        iteration = 0
      
        for word in fullLine.split("#"):
            if iteration == len(fullLine.split("#"))-1 and word == "":
                break
            iteration+=1
            if word == "":
                fullChangedLine=fullChangedLine+punctuation.pop(0)
                continue
            #build an array that represents the word with a "" for nothing, "י" for chirik, "ו" for halom or kamatz
            wordRepArray = getWordRepArray(word)
            #remove the remaining nikud
            noNikkudWord = ""
            for c in word:
                if not(c == "\u05B4" or c == "\u05B9" or c=="\u05BA" or c=="\u05C4" or c=="\u05C5" or c == kubutz or c == tsere or c == kamatz or c == chatefKamatz):
                    noNikkudWord = noNikkudWord+c
            word = noNikkudWord
            
            #build an array of positions of the nikkud
            index = 0
            positions = []
            for element in wordRepArray:
                if not(element == ""):
                    positions.append(index)
                index = index+1
            #find all combinations of the positions array and light up those positions by inserting a vav or a yud
            #https://www.adamsmith.haus/python/answers/how-to-find-all-combinations-of-a-list-in-python
            all_combinations = []
            for r in range(len(positions) + 1):
                combinations_object = itertools.combinations(positions, r)
                combinations_list = list(combinations_object)
                all_combinations += combinations_list
            #find all posible combinations of yuds and vavs
            allPossibleWords = []
            for pos in all_combinations:
                offset = 0
                changedWord = word
                for insertPos in pos:
                    changedWord = insert(wordRepArray[insertPos], changedWord, insertPos+offset+1)
                    offset+=1
                allPossibleWords.append(changedWord)  
            
            bestWord = findBestWord(allPossibleWords, allDicts)
            fullChangedLine = fullChangedLine + bestWord + punctuation.pop(0)

        nikudDeletedFile.write(fullChangedLine+"\n")
input.close()
print("Check NikudDeleted.txt file for the version with the nikud removed")


