package filmorate.app.rating;

import java.util.List;

public interface RatingDao {

    Rating getRatingById(Long ratingId);

    List<Rating> getRatings();
}
