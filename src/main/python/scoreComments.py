# -*- coding: utf-8 -*-
"""

Based on: https://github.com/megansquire/masteringDM/blob/master/ch5/scoreLinusEmail.py by Megan Squire

Use nltkDownload.py first to download the required data files for sentiment analysis.

Needs a python with some extras - you can use the community edition of Anaconda:
https://www.continuum.io/downloads

"""
from nltk.sentiment.vader import SentimentIntensityAnalyzer
from nltk import tokenize
import psycopg2.extras

updateScoreQuery = "UPDATE comments \
                    SET sentiment_score = %s, \
                    max_pos_score = %s, \
                    max_neg_score = %s \
                    WHERE id = %s"


db = psycopg2.connect(host='localhost', database='reddit', user='ingo', password='dev', port=5432)
updateDbConnection = psycopg2.connect(host='localhost', database='reddit', user='ingo', password='dev', port=5432)

updateCursor = updateDbConnection.cursor()

#selectCommentQuery = "DECLARE super_cursor BINARY CURSOR FOR " \
selectCommentQuery = "SELECT id, body FROM comments WHERE sentiment_score=0 and max_neg_score=0 and comments.max_pos_score=0"

selectCursor = db.cursor("reddit_cursor", cursor_factory=psycopg2.extras.DictCursor)
selectCursor.execute(selectCommentQuery)
commentCounter = 0

sid = SentimentIntensityAnalyzer()
for comment in selectCursor:
    id = comment[0]
    body = comment[1]

    # variables to hold the overall average compound score for message
    finalScore = 0
    roundedFinalScore = 0

    # variables to hold the highest positive score in the message
    # and highest negative score in the message
    maxPosScore = 0
    maxNegScore = 0

    # print("===")
    commentLines = tokenize.sent_tokenize(body)
    for line in commentLines:
        ss = sid.polarity_scores(line)
        # uncomment these lines if you want to print out sentences & scores
        '''
        line = line.replace('\n', ' ').replace('\r', '')
        print(line)
        for k in sorted(ss):
            print(' {0}: {1}\n'.format(k,ss[k]), end='')
        '''
        lineCompoundScore = ss['compound']
        finalScore += lineCompoundScore

        if ss['pos'] > maxPosScore:
            maxPosScore = ss['pos']
        elif ss['neg'] > maxNegScore:
            maxNegScore = ss['neg']

    # roundedFinalScore is the average compound score for the entire message
    commentLength = len(commentLines)
    if commentLength == 0:
        commentLength = 1
    roundedFinalScore = round(finalScore / commentLength, 4)
    # print("***Final comment Score", roundedFinalScore)
    # print("Most Positive Sentence Score:", maxPosScore)
    # print("Most Negative Sentence Score:", maxNegScore)

    # update table with calculated fields
    try:
        if roundedFinalScore != 0 or maxPosScore !=0 or maxNegScore != 0:
            updateCursor.execute(updateScoreQuery, (roundedFinalScore, maxPosScore, maxNegScore, id))
        commentCounter += 1
    except:
        updateDbConnection.rollback()
        print("Failed to update cursor.")
    if commentCounter % 1000 == 0:
        updateDbConnection.commit()
        print(commentCounter)
updateDbConnection.commit()
updateDbConnection.close()
db.close()

