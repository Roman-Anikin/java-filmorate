package filmorate.app.rating;

import java.util.List;

public interface RatingService {

    Rating getRatingById(Long ratingId);

    List<Rating> getRatings();

}
