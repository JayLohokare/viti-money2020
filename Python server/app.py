#Your Citi bank account 9987 has been debited with 100$ for transaction at Sears . Current balance: 5000$
#Your Citi bank account 9987 has been credited with 100000$ . Current balance: 50000$

#Car loan EMI payment of 1000$ for account XXX is due
#Car loan EMI payment of 1000$ for account XXX has been received

#Bill for postpaid ATT mobile of 50$ is due
#Bill for postpaid ATT mobile of 50$ has been received

# { "$", "at", "balance:", "account"}
# {"Citibank", "BOA", "JPMorgam", "HSBC"}
# {"credited", "debited", "EMI", "Bill"}
# {"received" , "due"}


#https://a4cbd55c.ngrok.io/createUser?id=jaylohokare&email=jaylohokare@gmail.com&name=Jay Lohokare
#https://a4cbd55c.ngrok.io/createUser?id=sbMemdc5084ae5edc83f9a5a77db760fbd56eca1&email=nandans16@gmail.com&name=Nandan Sukthankar



#http://127.0.0.1:5000/handleSMS?id=try&message=MESSAGE

#sbMemdc5084ae5edc83f9a5a77db760fbd56eca1

from __future__ import print_function 
from flask import Flask, request, redirect
import time
import datetime
from pymongo import MongoClient
from bson import Binary, Code
from bson.json_util import dumps

#Mongo Credentials
#username = admin
#password = Mongo2020Mongo
client = MongoClient('mongodb+srv://root:Money2020Mongo@money2020-e10jn.mongodb.net/test', 27017)
db = client.data

app = Flask(__name__)

@app.route('/')
def testApiLive():
    return "API is working"



@app.route('/getUserDetails',  methods=['POST', 'GET'])
def getUserDetails():
    requestData = request.get_json()
    id = requestData['id']

    print (id)
    userCollection = db.users
    for user in userCollection.find({'userId':id}):
        
        email = user['email']
        name = user['name']
        id = user['userId']

        endorsedBy = ""
        if 'endorsedBy' in user:
            endorsedBy = user['endorsedBy']

        socialScore = ""
        if 'likelihood' in user['socialData']:
            socialScore = user['socialData']['likelihood']

        work = "Unknown"
        if 'organizations' in user['socialData']:
            for i in user['socialData']['organizations']:
                if i['isPrimary'] == True:
                    work = i['title'] + " at " + i['name']

        location = ""
        if "demographics" in  user['socialData']:
            if "locationGeneral" in user['socialData']['demographics']:
                location = user['socialData']['demographics']["locationGeneral"]

        break

    data = {
        'id' : id,
        'email' : email,
        'name' : name,
        "endorsedBy" : endorsedBy,
        "socialScore" : socialScore,
        "work" : work,
        "location" : location,
        "photo" : user['photo'],
        "creditScore": user['creditScore']

    }
    
    print (data)

    return dumps(data)



@app.route('/endorse', methods=['POST', 'GET'])
def endorse():
    args = request.args
    id = args['id']
    by = args['by']

    userCollection = db.users
    for user in userCollection.find({'userId' : id}):
        user['endorsedBy'] = by
        userCollection.update({"userId" : id}, user)
        break

    return "Success"



@app.route('/requestLoan', methods=['POST', 'GET'])
def requestLoans():
    requestData = request.get_json()
    id = requestData['id']
    amount = requestData['amount']

    creditScore, photo, name =  0, 0,0

    endorsedBy = ""
    userCollection = db.users
    for user in userCollection.find({'userId':id}):
        creditScore = user['creditScore']
        photo = user['photo']
        name = user['name']
        if 'endorsedBy' in user:
            endorsedBy = user['endorsedBy']
        break
    
    loansCollection = db.loans
    loansCollection.insert_one({
        "amount" : amount,
        "creditScore" : creditScore,
        "userId" : id,
        "photo" : photo,
        "name" : name,
        "endorsedBy": endorsedBy
    })

    return "Success"





@app.route('/requestLoanTest', methods=['POST', 'GET'])
def requestLoansTest():
    args = request.args
    id = args['id']
    amount = args['amount']

    
    endorsedBy = ""
    userCollection = db.users
    for user in userCollection.find({'userId': id}):
        print (user)
        creditScore = user['creditScore']
        photo = user['photo']
        name = user['name']
        if 'endorsedBy' in user:
            endorsedBy = user['endorsedBy']
        break
    
    loansCollection = db.loans
    loansCollection.insert_one({
        "amount" : amount,
        "creditScore" : creditScore,
        "userId" : id,
        "photo" : photo,
        "name" : name,
        "endorsedBy":endorsedBy
    })

    return "Success"


@app.route('/getLoans', methods=['POST', 'GET'])
def getLoansList():    
    loansCollection = db.loans
    returnArray = []

    for loan in loansCollection.find():
        returnArray.append(loan)
        # print (loan)
    
    data = {}
    data['data'] = returnArray 
    return dumps(data)




@app.route('/createUser', methods=['POST', 'GET'])
def createUser():
    args = request.get_json()

    # args = request.args

    json = {
        "userId" : args['id'],
        "email" :args['email'].lower(),
        "name" :args['name']
    }
    usersCollection = db.users
    usersCollection.insert_one(json)
    result = {"result":"Success"}
    return dumps(result)




@app.route('/handleSMS' , methods=['POST', 'GET'])
def handleSMS():
    
    # requestData = request.args
       
    requestData = request.get_json()
    message = requestData['message'].lower().split()
    id = requestData['id']

    print (id, " ", message)

    #Get time stamp
    ts = time.time()
    st = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')

    if "credited" in message:
        accountNumber = message[message.index("account") + 1]
        balance = message[message.index("balance:") + 1]
        amount = message[message.index("credited") + 2]
        bank = message[message.index("bank") - 1]

        json = {
            "userId" : id,
            "account" : accountNumber,
            "type" : "credit",
            "balance" : balance,
            "amount" : amount,
            "bank": bank,
            "timeStamp": st,
            "date": datetime.datetime.fromtimestamp(ts).strftime('%d') ,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y')
        }
        rawCollection = db.raw
        rawCollection.insert_one(json)

        print("Got credit transcation")
        print ("Account no " + accountNumber)
        print ("Balance " + balance)
    
    elif "debited" in message:
        accountNumber = message[message.index("account") + 1]
        balance = message[message.index("balance:") + 1]
        amount = message[message.index("debited") + 2]
        bank = message[message.index("bank") - 1]

        json = {
            "userId" : id,
            "account" : accountNumber,
            "type" : "debit",
            "bank" : bank,
            "balance" : balance,
            "amount" : amount,
            "timeStamp": st,
            "date": datetime.datetime.fromtimestamp(ts).strftime('%d') ,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y')
        }
        rawCollection = db.raw
        rawCollection.insert_one(json)
    
        
        print ("Got debit transcation")
        print ("Account no " + accountNumber)
        print ("Balance " + balance)
        
    elif "bill" in message:
        for i in message:
            if '$' in i:
                billAmount = i
                break
        startIndex = message.index("for") + 1
        endIndex = message.index("of") 
        purpose = " ".join(message[startIndex : endIndex])
        status = message[-1]

        json = {
            "userId" : id,
            "purpose" : purpose,
            "type" : "bill",
            "status" : status,
            "amount" : billAmount,
            "timeStamp": st,
            "date": datetime.datetime.fromtimestamp(ts).strftime('%d') ,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y')
        }

        compareJson = {
            "userId" : id,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y'),
            "type" : "bill",
            "purpose" : purpose
        }

        rawCollection = db.raw
        result = rawCollection.update(compareJson, json, upsert = True)

        print ("Got bill transcation")
        print ("Bill amount " + billAmount)
        print ("Bill purpose " + purpose)
        print ("Bill status " + status)

    
    elif "emi" in message:
        accountNumber = message[message.index("account") + 1]
        for i in message:
            if '$' in i:
                amount = i
                break
        status = message[-1]

        json = {
            "userId" : id,
            "account" : accountNumber,
            "type" : "emi",
            "status" : status,
            "amount" : amount,
            "timeStamp": st,
            "date": datetime.datetime.fromtimestamp(ts).strftime('%d') ,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y')
        }

        compareJson = {
            "userId" : id,
            "account" : accountNumber,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y'),
            "type" : "emi"
        }

        rawCollection = db.raw
        result = rawCollection.update(compareJson, json, upsert = True)

        print ("Got emi transcation")
        print ("Account no " + accountNumber)
        print ("EMI Amount " + amount)
        print ("EMI status " + status)
    
    result = {"result" :"Success"}
    return dumps(result)



@app.route('/handleSMSTest' , methods=['POST', 'GET'])
def handleSMSTest():
    # message = request.values.get('message').lower().split()
    # id  = request.values.get('id')
    args = request.args
    message = args['message'].lower().split()
    id = args['id']


    #Get time stamp
    ts = time.time()
    st = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')

    if "credited" in message:
        accountNumber = message[message.index("account") + 1]
        balance = message[message.index("balance:") + 1]
        amount = message[message.index("credited") + 2]
        bank = message[message.index("bank") - 1]

        json = {
            "userId" : id,
            "account" : accountNumber,
            "type" : "credit",
            "balance" : balance,
            "amount" : amount,
            "bank": bank,
            "timeStamp": st,
            "date": datetime.datetime.fromtimestamp(ts).strftime('%d') ,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y')
        }
        rawCollection = db.raw
        rawCollection.insert_one(json)

        print("Got credit transcation")
        print ("Account no " + accountNumber)
        print ("Balance " + balance)
    
    elif "debited" in message:
        accountNumber = message[message.index("account") + 1]
        balance = message[message.index("balance:") + 1]
        amount = message[message.index("debited") + 2]
        bank = message[message.index("bank") - 1]

        json = {
            "userId" : id,
            "account" : accountNumber,
            "type" : "debit",
            "bank" : bank,
            "balance" : balance,
            "amount" : amount,
            "timeStamp": st,
            "date": datetime.datetime.fromtimestamp(ts).strftime('%d') ,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y')
        }
        rawCollection = db.raw
        rawCollection.insert_one(json)
    
        
        print ("Got debit transcation")
        print ("Account no " + accountNumber)
        print ("Balance " + balance)
        
    elif "bill" in message:
        for i in message:
            if '$' in i:
                billAmount = i
                break
        startIndex = message.index("for") + 1
        endIndex = message.index("of") 
        purpose = " ".join(message[startIndex : endIndex])
        status = message[-1]

        json = {
            "userId" : id,
            "purpose" : purpose,
            "type" : "bill",
            "status" : status,
            "amount" : billAmount,
            "timeStamp": st,
            "date": datetime.datetime.fromtimestamp(ts).strftime('%d') ,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y')
        }

        compareJson = {
            "userId" : id,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y'),
            "type" : "bill",
            "purpose" : purpose
        }

        rawCollection = db.raw
        result = rawCollection.update(compareJson, json, upsert = True)

        print ("Got bill transcation")
        print ("Bill amount " + billAmount)
        print ("Bill purpose " + purpose)
        print ("Bill status " + status)

    
    elif "emi" in message:
        accountNumber = message[message.index("account") + 1]
        for i in message:
            if '$' in i:
                amount = i
                break
        status = message[-1]

        json = {
            "userId" : id,
            "account" : accountNumber,
            "type" : "emi",
            "status" : status,
            "amount" : amount,
            "timeStamp": st,
            "date": datetime.datetime.fromtimestamp(ts).strftime('%d') ,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y')
        }

        compareJson = {
            "userId" : id,
            "account" : accountNumber,
            "month": datetime.datetime.fromtimestamp(ts).strftime('%m'),
            "year" : datetime.datetime.fromtimestamp(ts).strftime('%Y'),
            "type" : "emi"
        }

        rawCollection = db.raw
        result = rawCollection.update(compareJson, json, upsert = True)

        print ("Got emi transcation")
        print ("Account no " + accountNumber)
        print ("EMI Amount " + amount)
        print ("EMI status " + status)
    
    return "Success"


if __name__ == '__main__':
    app.run(debug=True)
