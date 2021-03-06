package CSCI5308.GroupFormationTool.Question;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import CSCI5308.GroupFormationTool.Database.CallStoredProcedure;

public class QuestionDB implements IQuestionPersistance {
    private Logger log = Logger.getLogger(QuestionDB.class.getName());
    @Override
    public List<Questions> loadAllQuestionTitlesByInstructorID(long instructorID) {
        List<Questions> questions = new ArrayList<>();
        CallStoredProcedure proc = null;
        try {
            proc = new CallStoredProcedure("spLoadAllQuestionsByInstructorID(?)");
            proc.setParameter(1, instructorID);
            ResultSet results = proc.executeWithResults();
            if (null != results) {
                while (results.next()) {
                    Questions question = new Questions();
                    question.setQuestionId(results.getInt("questionID"));
                    question.setTitle(results.getString("title"));
                    question.setQuestionText(results.getString("questionText"));
                    question.setDateCreated(results.getDate("dateCreated"));
                    questions.add(question);
                }
            }

        } catch (SQLException e) {
            log.log(Level.SEVERE, "Encountered SQL Exception while loading questions for instructor " + instructorID);
        } finally {
            if (null != proc) {
                proc.cleanup();
            }
        }
        log.log(Level.INFO, "Questions loaded fro DB for instructor " + instructorID);
        return questions;
    }

    @Override
    public void loadQuestionById(Integer questionID, Questions que) {
        CallStoredProcedure proc = null;
        try {
            proc = new CallStoredProcedure("spFindQuestionByID(?)");
            proc.setParameter(1, questionID);
            ResultSet results = proc.executeWithResults();
            if (null != results) {
                while (results.next()) {
                    que.setQuestionId(questionID);
                    que.setTitle(results.getString("title"));
                    que.setQuestionText(results.getString("questionText"));
                    que.setDateCreated(results.getDate("dateCreated"));
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Encountered SQL Exception while loading option for question " + questionID);
        } finally {
            if (null != proc) {
                proc.cleanup();
            }
        }
    }

    @Override
    public boolean deleteQuestion(Integer questionID) {
        CallStoredProcedure proc = null;
        try {
            proc = new CallStoredProcedure("spDeleteQuestion(?)");
            proc.setParameter(1, questionID);
            proc.execute();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Encountered SQL Exception while deleting question " + questionID);
            return false;
        } finally {
            if (null != proc) {
                proc.cleanup();
            }
        }
        log.log(Level.INFO, "Deleted Question " + questionID + " from DB");
        return true;
    }
}
