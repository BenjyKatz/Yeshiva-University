#Benjy Katz
#EDA: https://colab.research.google.com/drive/1jUSF4vwt6BLiilRjfTZyYGWmtCYXkvEs?usp=sharing
#collab to experiment with different fine tuned parameters: https://colab.research.google.com/drive/1tykW9ckBFGJ_RZbr0WusVLrDptpeFb4H?usp=sharing


#roc auc:  0.8540394574880751
#train roc auc:  0.8677415648319996
#

from lib2to3.pgen2.token import EQUAL
import pickle
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.metrics import roc_auc_score, accuracy_score
import joblib
import xgboost as xgb


from flask import Flask, request, jsonify

start_all_columns ={
"TransactionAmt": -0.15008982651885022,
"card1": 11106.0,
"card2": 100.0,
"card3": 150.0,
"card5": 226.0,
"V257": 1.0,
"V246": 1.0,
"V244": 1.0,
"V242": 1.0,
"V201": 1.0,
"V200": 1.0,
"V189": 1.0,
"V188": 1.0,
"V258": 1.0,
"V45": 1.116950357972595,
"V158": 1.0,
"V156": 1.0,
"V149": 1.0,
"V228": 1.0,
"V44": 1.0805556872599686,
"V86": 1.0635082899402613,
"V87": 1.0973229402932627,
"V170": 1.0,
"V147": 0.0,
"V52": 0.1809018064577308,
"ProductCD_C": 0.0,
"ProductCD_H": 1.0,
"ProductCD_R": 0.0,
"ProductCD_S": 0.0,
"ProductCD_W": 0.0,
"card4_american express": 0.0,
"card4_discover": 0.0,
"card4_mastercard": 0.0,
"card4_visa": 0.0,
"card6_charge card": 0.0,
"card6_credit": 0.0,
"card6_debit": 0.0,
"card6_debit or credit": 0.0,
"P_emaildomain_aim.com": 0.0,
"P_emaildomain_anonymous.com": 0.0,
"P_emaildomain_aol.com": 0.0,
"P_emaildomain_att.net": 0.0,
"P_emaildomain_bellsouth.net": 0.0,
"P_emaildomain_cableone.net": 0.0,
"P_emaildomain_centurylink.net": 0.0,
"P_emaildomain_cfl.rr.com": 0.0,
"P_emaildomain_charter.net": 0.0,
"P_emaildomain_comcast.net": 0.0,
"P_emaildomain_cox.net": 0.0,
"P_emaildomain_earthlink.net": 0.0,
"P_emaildomain_embarqmail.com": 0.0,
"P_emaildomain_frontier.com": 0.0,
"P_emaildomain_frontiernet.net": 0.0,
"P_emaildomain_gmail": 0.0,
"P_emaildomain_gmail.com": 0.0,
"P_emaildomain_gmx.de": 0.0,
"P_emaildomain_hotmail.co.uk": 0.0,
"P_emaildomain_hotmail.com": 0.0,
"P_emaildomain_hotmail.de": 0.0,
"P_emaildomain_hotmail.es": 0.0,
"P_emaildomain_hotmail.fr": 0.0,
"P_emaildomain_icloud.com": 0.0,
"P_emaildomain_juno.com": 0.0,
"P_emaildomain_live.com": 0.0,
"P_emaildomain_live.com.mx": 0.0,
"P_emaildomain_live.fr": 0.0,
"P_emaildomain_mac.com": 0.0,
"P_emaildomain_mail.com": 0.0,
"P_emaildomain_me.com": 0.0,
"P_emaildomain_msn.com": 0.0,
"P_emaildomain_netzero.com": 0.0,
"P_emaildomain_netzero.net": 0.0,
"P_emaildomain_optonline.net": 0.0,
"P_emaildomain_outlook.com": 0.0,
"P_emaildomain_outlook.es": 0.0,
"P_emaildomain_prodigy.net.mx": 0.0,
"P_emaildomain_protonmail.com": 0.0,
"P_emaildomain_ptd.net": 0.0,
"P_emaildomain_q.com": 0.0,
"P_emaildomain_roadrunner.com": 0.0,
"P_emaildomain_rocketmail.com": 0.0,
"P_emaildomain_sbcglobal.net": 0.0,
"P_emaildomain_sc.rr.com": 0.0,
"P_emaildomain_servicios-ta.com": 0.0,
"P_emaildomain_suddenlink.net": 0.0,
"P_emaildomain_twc.com": 0.0,
"P_emaildomain_verizon.net": 0.0,
"P_emaildomain_web.de": 0.0,
"P_emaildomain_windstream.net": 0.0,
"P_emaildomain_yahoo.co.jp": 0.0,
"P_emaildomain_yahoo.co.uk": 0.0,
"P_emaildomain_yahoo.com": 0.0,
"P_emaildomain_yahoo.com.mx": 0.0,
"P_emaildomain_yahoo.de": 0.0,
"P_emaildomain_yahoo.es": 0.0,
"P_emaildomain_yahoo.fr": 0.0,
"P_emaildomain_ymail.com": 0.0
}


top_columns = ["V257","V246", "V244","V242","V201","V200","V189","V188","V258",
"V45","V158","V156","V149","V228","V44","V86","V87","V170","V147","V52"]

_KEEP_COLUMNS_MODEL_TxnPcd = ['TransactionAmt', 'ProductCD']

_KEEP_COLUMNS_MODEL_1_5 = ['ProductCD', 'TransactionAmt', 'card1', 'card2', 'card3', 'card4',
                      'card5', 'card6', 'P_emaildomain']


transaction_mean = 135.06408031293395
transaction_std = 240.4606365631347

app = Flask(__name__)
def send_email(is_fraud, recipient):
    import smtplib
    from email.mime.text import MIMEText
    from email.mime.multipart import MIMEMultipart
    from email.mime.application import MIMEApplication

    # Email server settings (for Gmail)
    smtp_server = 'smtp.gmail.com'
    smtp_port = 587  # For TLS
    smtp_user = 'benjykatz12@gmail.com'
    smtp_password = 'sgmj ncjx nldv zuuq'

    # Create a connection to the SMTP server
    server = smtplib.SMTP(smtp_server, smtp_port)

    # Start a secure connection to the SMTP server
    server.starttls()

    # Log in to your email account
    server.login(smtp_user, smtp_password)

    # Create an email message
    from_email = 'benjykatz12@gmail.com'
    to_email = recipient
    subject = 'Credit card check'
    body = 'The transaction is fraud?'+is_fraud

    msg = MIMEMultipart()
    msg['From'] = from_email
    msg['To'] = to_email
    msg['Subject'] = subject
    msg.attach(MIMEText(body, 'plain'))

    # You can also attach files if needed
    # file_attachment = 'path_to_your_file.pdf'
    # with open(file_attachment, 'rb') as file:
    #     attachment = MIMEApplication(file.read(), _subtype="pdf")
    #     attachment.add_header('Content-Disposition', 'attachment', filename='file.pdf')
    #     msg.attach(attachment)

    # Send the email
    server.sendmail(from_email, to_email, msg.as_string())

    # Close the connection
    server.quit()
@app.route('/predict', methods=['POST'])
def predict():
    all_columns = start_all_columns.copy()
    print("call made")
    try:
        data = request.get_json()  # Get JSON data from the request
        print(data)
        if data is not None:
            # Process the JSON data (you can customize this part)
            input_df = pd.DataFrame(data, index=[0])
            email_to = input_df.email[0]
            all_columns["TransactionAmt"] = ((float)(input_df.TransactionAmt) - transaction_mean) / transaction_std
            print("better")
            all_columns["card1"] = input_df.card1[0]
            all_columns["card2"] = input_df.card2[0]
            print("better2")
            all_columns["card3"] = input_df.card3[0]
            all_columns["card5"] = input_df.card5[0]
            if len(input_df.card4)>0:
                all_columns["card4_"+str(input_df.card4[0])] = 1
            if len(input_df.card6)>0:
                all_columns["card6_"+str(input_df.card6[0])] = 1
            if(len(input_df.ProductCD)> 0):
                all_columns["ProductCD_"+str(input_df.ProductCD[0])] = 1
            if(len(input_df.P_emaildomain)> 0):
                all_columns["P_emaildomain_"+str(input_df.P_emaildomain[0])] = 1
            print("card4_"+str(input_df.card4[0]))
            print("card6_"+str(input_df.card6[0]))
            print("ProductCD_"+str(input_df.ProductCD[0]))
            print("P_emaildomain_"+str(input_df.P_emaildomain[0]))


            #X = df[top_columns +_KEEP_COLUMNS_MODEL_1_5]

            #x_train_norm_df = df[_KEEP_COLUMNS_MODEL_1_5+top_columns]
            #x_train_norm_df = df[_KEEP_COLUMNS_MODEL_1_5]

            #x_train_norm_df.TransactionAmt = ((float)(x_train_norm_df.TransactionAmt) - transaction_mean) / transaction_std
           
            #print(X_train)
            #print(x_train_norm_df)
            norm_data = ['ProductCD', 'card4','card6', 'P_emaildomain']

            #x_train_oh_df = pd.get_dummies(x_train_norm_df, columns = norm_data)
            print("database")
            #print(x_train_oh_df)
            dataFrame = pd.DataFrame(all_columns, index = [0])
            print(dataFrame)
            with open('fraud_model.pkl', 'rb') as file:
                loaded_model = pickle.load(file)
            print("loaded model")


            #pred = loaded_model.predict_proba(x_train_oh_df.values)[:,1]
            pred = loaded_model.predict_proba(dataFrame.values)[:,1]>.35
            print("predicition")
            print(loaded_model.predict_proba(dataFrame.values)[:,1])
            print(pred[0])
            #send_email(str(pred[0]), email_to)
            return jsonify({"result": str(pred[0])})
        else:
            print("error happened")
            return jsonify({"error": "Invalid JSON data"})
    except Exception as e:
        print("exception happened")
        print(e)
        return jsonify({"error": str(e)})

if __name__ == '__main__':
    app.run()



