package bnb;

import java.sql.SQLException;
import java.util.ArrayList;

public class Comment {
  private int cid;
  private String comment;
  private int rating;
  private int rid;
  private int hid;
  private String reviewer;

  public Comment(int cid, String comment, int rating, int rid, int hid, String reviewer) {
    this.cid = cid;
    this.comment = comment;
    this.rating = rating;
    this.rid = rid;
    this.hid = hid;
    this.reviewer = reviewer;
  }

  public static void createComment(DAO dao, String comment, int rating, int rid, int hid, String reviewer) throws IllegalArgumentException, SQLException {
    dao.createComment(hid, rid, comment, rating, reviewer);
  }
  public static Comment getComment(DAO dao, int cid) throws SQLException {
    Comment result = dao.getComment(cid);
    if (result == null) {
      throw new SQLException("Comment not found");
    }
    return result;
  }

  public static ArrayList<Comment> getCommentsByUser(DAO dao, int uid) throws SQLException {
    ArrayList<Comment> result = dao.getCommentsByUser(uid);
    if (result.isEmpty()) {
      throw new SQLException("No comments found for User");
    }
    return result;
  }

}
