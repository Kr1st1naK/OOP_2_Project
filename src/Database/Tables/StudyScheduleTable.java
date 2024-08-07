package Database.Tables;

import java.sql.*;
import Database.Common.*;
import Database.Models.StudySchedule;

public class StudyScheduleTable extends BaseTable<StudySchedule>{

    public StudyScheduleTable(Connection connection){
        super(connection);
    }

    private static final String TABLE_NAME = "StudySchedule";
    private static final String INSERT_QUERY="INSERT INTO StudySchedule (user_id,created_at,updated_at) VALUES (?,?,?)";

    /**
     * Returns the name of the table.
     *
     * @return the name of the table
     */
    @Override
    protected String getTableName(){
        return TABLE_NAME;
    }
    
    
    /**
     * Maps a ResultSet object to a StudySchedule object by extracting the values from the ResultSet
     * for the columns "id", "user_id", "created_at", and "updated_at". 
     *
     * @param  rs  the ResultSet object containing the data to be mapped
     * @return      a StudySchedule object with the extracted values
     * @throws SQLException if a database access error occurs
     */
    @Override
    protected StudySchedule mapResultSetToEntity(ResultSet rs) throws SQLException{
        int id=rs.getInt("id");
        int user_id=rs.getInt("user_id");
        Timestamp created_at=rs.getTimestamp("created_at");
        Timestamp updated_at=rs.getTimestamp("updated_at");
        return new StudySchedule(id,user_id, created_at, updated_at);
    }


    /**
     * Inserts a StudySchedule into the database.
     *
     * @param  studySchedule  the StudySchedule object to insert
     */
    public void insert(StudySchedule studySchedule) {
        
        try (PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studySchedule.getUser_id());
            ps.setTimestamp(2, studySchedule.getCreated_at());
            ps.setTimestamp(3, studySchedule.getUpdated_at());
            ps.executeUpdate();
    
            // Retrieve the generated key and set it in the studySchedule object
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                studySchedule.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            String errorMessage = "Error inserting new record into StudySchedule";
            DatabaseLogger.logError(errorMessage, e);
            throw new DatabaseException(errorMessage, e);
        }
    }
    
    

}