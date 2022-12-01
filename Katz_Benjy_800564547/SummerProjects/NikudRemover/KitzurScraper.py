from selenium import webdriver
from selenium.webdriver.common.by import By
#from BeautifulSoup import BeautifulSoup
import pandas as pd
PATH = "/usr/local/bin/chromedriver"
driver = webdriver.Chrome(PATH)

#driver.get("https://he.wikisource.org/wiki/%D7%A7%D7%99%D7%A6%D7%95%D7%A8_%D7%A9%D7%95%D7%9C%D7%97%D7%9F_%D7%A2%D7%A8%D7%95%D7%9A_%D7%9E%D7%A0%D7%95%D7%A7%D7%93_-_%D7%90")#Nikud
#driver.get("https://he.wikisource.org/wiki/%D7%A7%D7%99%D7%A6%D7%95%D7%A8_%D7%A9%D7%95%D7%9C%D7%97%D7%9F_%D7%A2%D7%A8%D7%95%D7%9A_%D7%90")#No Nikud
#driver.get("https://he.wikisource.org/wiki/%D7%A7%D7%99%D7%A6%D7%95%D7%A8_%D7%A9%D7%95%D7%9C%D7%97%D7%9F_%D7%A2%D7%A8%D7%95%D7%9A_%D7%9E%D7%A0%D7%95%D7%A7%D7%93_-_%D7%A6%D7%91")#nikud tzadibet
driver.get("https://he.wikisource.org/wiki/%D7%A7%D7%99%D7%A6%D7%95%D7%A8_%D7%A9%D7%95%D7%9C%D7%97%D7%9F_%D7%A2%D7%A8%D7%95%D7%9A_%D7%A7%D7%A1%D7%93")#kuf samech daled nonikud
for i in range(221):
    content = driver.page_source
    par = driver.find_element("id", "mw-content-text")
    print()
    print()
    print(par.text)
    try:
        nextButton = driver.find_element(By.XPATH,'//*[@id="mw-content-text"]/div[1]/center/div/center/span/b/a[3]')
    except:
        try:
            nextButton = driver.find_element(By.XPATH,'//*[@id="mw-content-text"]/div[1]/center[1]/p[1]/big/b/a[4]')
        except:
            try:
                nextButton = driver.find_element(By.XPATH,'//*[@id="mw-content-text"]/div[1]/div/center/p[1]/big/b/a[4]')
            except:
                try:
                    nextButton = driver.find_element(By.XPATH,'//*[@id="mw-content-text"]/div[1]/center/a[3]')
                except:
                    try:
                        nextButton = driver.find_element(By.XPATH,'//*[@id="mw-content-text"]/div[1]/div/p/a[4]')
                    except:
                        try: 
                            nextButton = driver.find_element(By.XPATH,'//*[@id="mw-content-text"]/div[1]/center/p[1]/big/b/a[4]')
                        except:
                            nextButton = driver.find_element(By.XPATH,'//*[@id="mw-content-text"]/div[1]/div/p/a[4]')
    nextButton.click()
