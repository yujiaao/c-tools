package r2dbc;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

/**
 * @author xwx
 */
public interface ReactiveClientUserSortingRepository extends ReactiveSortingRepository<ClientUser,String> {

    @Modifying
    @Query("insert into client_user (user_id,nick_name,phone_number,gender) values (:userId,:nickName,:phoneNumber,:gender)")
    Mono<Integer> addClientUser(String userId, String nickName, String phoneNumber, Integer gender);
}
