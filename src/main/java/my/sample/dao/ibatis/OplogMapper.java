package my.sample.dao.ibatis;

import my.sample.dao.model.Oplog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OplogMapper {

    @Select("select id as id, user_id as userId, op as op, op_time as opTime from sample_oplog where id= #{id}")
    Oplog findById(Integer id);

    @Insert("insert into sample_oplog(id, user_id, op, op_time) values (#{id}, #{userId}, #{op}, #{opTime})")
    int insert(Oplog oplog);

    int insertBatch(List<Oplog> oplogList);
}
