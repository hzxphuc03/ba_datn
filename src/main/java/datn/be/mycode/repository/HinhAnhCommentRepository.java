package datn.be.mycode.repository;

import datn.be.mycode.entity.HinhAnhComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HinhAnhCommentRepository extends JpaRepository<HinhAnhComment,Long> {


}
