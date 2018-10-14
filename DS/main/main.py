'''
Created on Oct 12, 2018

@author: Hasham Ul Haq
'''

#importing libraries
import pandas as pd
import numpy as np

from flask import Flask, render_template
app = Flask(__name__)

## function for splitting data into train and test set
def split_func(dataframe):

    indexes = range(dataframe.shape[0])
    np.random.shuffle(indexes)

    splitpoint = int(dataframe.shape[0] * .8)

    train = dataframe.iloc[indexes[:splitpoint]]
    test = dataframe.iloc[indexes[splitpoint:]]
    
    y_train = train['Class']
    x_train = train.drop('Class', axis=1)
    
    y_test = test['Class']
    x_test = test.drop('Class', axis=1)
    
    ## splitting using sklearn//could use stratification as an added parameter ##
    #from sklearn.model_selection import train_test_split
    #train, test = train_test_split(df, test_size=0.2)

    
    return x_train, y_train, x_test, y_test


def accuracy_func(results, actual):
    return np.sum(results == actual)/float(results.shape[0]) *100

 
@app.route("/")
def hello():

    x_testn = (x_test-mean)/rang
    res = model.predict(x_testn)
    
    Accuracy = accuracy_func(res, y_test)
    index = y_test == 1
    Recall = accuracy_func(res[index],y_test[index])
    
    print Accuracy
    print Recall
    
    
    accuracyValue = Accuracy
    recallValue = Recall
    featureImportance = model.feature_importances_*100
 
    return render_template(
        'layout.html',**locals())
  
if __name__ == "__main__":
        
    ## reading data from file
    data = pd.read_csv('./creditcard.csv')
    print 'Dimensions of data:',data.shape
    print 'Column Names:', data.columns
    #data.describe()
    
    x_train, y_train, x_test, y_test = split_func(data)
    print 'Training set:', x_train.shape
    print 'Test set:',x_test.shape
        
    print '\n Checking to ensure that both training and testing set have 80-20 split for the minor class'
    print 'Training set:',y_train.value_counts()
    print 'Test set:',y_test.value_counts()
    
    
    print '\n==Normalizing Data=='
    mean = x_train.mean()
    rang = x_train.max() - x_train.min()
    x_train = (x_train-mean)/rang
    
    print '\n=== Training Classifier ==='
    
    from sklearn.ensemble import RandomForestClassifier
    from sklearn.utils import class_weight
    class_weights = class_weight.compute_class_weight('balanced',
                                                 np.unique(data['Class']),
                                                 data['Class'])

    
    model = RandomForestClassifier(n_estimators=25, 
                                   class_weight={0:class_weights[0],1:class_weights[1]})
    model.fit(x_train, y_train)  
        
    
    # if you want to write the classifier to the disk
    #import pickle
    #output = open('classifier.pkl', 'wb')
    #pickle.dump(model, output)
    #output.close()
    
    #Other experiments
    #from sklearn.ensemble import GradientBoostingClassifier
    #model = GradientBoostingClassifier()
    #model.fit(x_train, y_train)

    app.run()