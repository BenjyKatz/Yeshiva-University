from selenium import webdriver
#from BeautifulSoup import BeautifulSoup
import pandas as pd
PATH = "/usr/local/bin/chromedriver"
driver = webdriver.Chrome(PATH)


driver.get("https://www.daf-yomi.com/Dafyomi_Page.aspx?vt=2&massechet=283&amud=3&fs=0")

for i in range(3630):
    content = driver.page_source
    par = driver.find_element("id", "ContentPlaceHolderMain_divTextWrapper")
    print()
    print()
    print(par.text)
    nextButton = driver.find_element("id", "ContentPlaceHolderMain_ancNext")
    nextButton.click()
