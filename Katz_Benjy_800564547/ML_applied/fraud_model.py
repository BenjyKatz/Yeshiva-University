#Benjy Katz
#EDA: https://colab.research.google.com/drive/1jUSF4vwt6BLiilRjfTZyYGWmtCYXkvEs?usp=sharing
#collab to experiment with different fine tuned parameters: https://colab.research.google.com/drive/1tykW9ckBFGJ_RZbr0WusVLrDptpeFb4H?usp=sharing


#roc auc:  0.8540394574880751
#train roc auc:  0.8677415648319996
#

import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.metrics import roc_auc_score, accuracy_score
import joblib
import xgboost as xgb
np.random.seed(314159)
train_txn = pd.read_csv('train_transaction.csv')

clf = xgb.XGBClassifier(
    n_estimators=190, #30
    max_depth=6,#8
    learning_rate=0.05,
    subsample=0.9,
    colsample_bytree=0.9,
    missing=-999,
    random_state=2,
    #tree_method='gpu_hist', # set runtime to gpu to take advantage
    scale_pos_weight=10.0
)

top_columns = ["V257","V246", "V244","V242","V201","V200","V189","V188","V258",
"V45","V158","V156","V149","V228","V44","V86","V87","V170","V147","V52"]
#top_columns = ["V257","V246", "V244","V242"]
_KEEP_COLUMNS_MODEL_TxnPcd = ['TransactionAmt', 'ProductCD']

_KEEP_COLUMNS_MODEL_1_5 = ['ProductCD', 'TransactionAmt', 'card1', 'card2', 'card3', 'card4',
                      'card5', 'card6', 'P_emaildomain']
X = train_txn[top_columns +_KEEP_COLUMNS_MODEL_1_5]
y = train_txn["isFraud"]

X_train, X_test, y_train, y_test = train_test_split(X,y, test_size=0.25)


x_train_norm_df = X_train[_KEEP_COLUMNS_MODEL_1_5+top_columns]
x_test_norm_df = X_test[_KEEP_COLUMNS_MODEL_1_5+top_columns]

x_train_norm_df.TransactionAmt = (x_train_norm_df.TransactionAmt - X_train.TransactionAmt.mean()) / X_train.TransactionAmt.std()
x_test_norm_df.TransactionAmt = (x_test_norm_df.TransactionAmt - X_test.TransactionAmt.mean()) / X_test.TransactionAmt.std()
#print(X_train)
#print(x_train_norm_df)
norm_data = ['ProductCD', 'card4','card6', 'P_emaildomain']

x_test_oh_df = pd.get_dummies(x_test_norm_df, columns = norm_data)
x_train_oh_df = pd.get_dummies(x_train_norm_df, columns = norm_data)





from sklearn.metrics import confusion_matrix, roc_auc_score, f1_score, accuracy_score
clf.fit(x_train_oh_df.values,y_train.values,eval_metric="auc")
model_filename = 'fraud_model.pkl'
joblib.dump(clf, model_filename)
print("the model can be found in fraud_model.pkl for future use")

preds = clf.predict_proba(x_test_oh_df.values)[:,1]

print("roc auc: ",roc_auc_score(y_test, preds))
preds_train = clf.predict_proba(x_train_oh_df.values)[:,1]
print("train roc auc: ",roc_auc_score(y_train, preds_train))
