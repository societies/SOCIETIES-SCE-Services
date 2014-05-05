package ac.hw.services.collabquiz.entities;


import javax.persistence.*;
/*
 * Copyright (coffee) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

@Entity
@Table(name = "question")
public class Question {

    @Id
    @Column(name = "question_id")
    private int questionID;
    
    @Column(name = "question_text")
    private String questionText;
    
    @Column(name = "answer_1")
    private String answer1;
    
    @Column(name = "answer_2")
    private String answer2;
    
    @Column(name = "answer_3")
    private String answer3;
    
    @Column(name = "answer_4")
    private String answer4;

    @Column(name = "correct_answer")
    private int correctAnswer;
    
    @Column(name = "category_ID")
    private int categoryID;
    
    @Column(name = "points_if_correct")
    private int pointsIfCorrect;

    public Question() {
        this.questionID = -1;
        this.pointsIfCorrect = 1;
        this.questionText = "[New question]";
        this.answer1 = "[Answer 1]";
        this.answer2 = "[Answer 2]";
        this.answer3 = "[Answer 3]";
        this.answer4 = "[Answer 4]";
        this.correctAnswer = 1;
        this.categoryID = -1;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

  
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

   
    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    /**
     * 1-based answer index (1-4)
     */
    public int getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * 1-based answer index (1-4)
     */
    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getPointsIfCorrect() {
        return pointsIfCorrect;
    }

    public void setPointsIfCorrect(int pointsIfCorrect) {
        this.pointsIfCorrect = pointsIfCorrect;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof Question) {
    		if(this.questionID == ((Question) obj).getQuestionID()) {
    			return true;
    		}
    	} else if(obj instanceof AnsweredQuestions) {
    		if(this.questionID == ((AnsweredQuestions) obj).getQuestionID()) {
    			return true;
    		}
    	}
    	return false;
    }

}
