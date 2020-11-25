package my.sample.dao.ibatis;

import my.sample.dao.model.Oplog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OplogMapper {

    @Select("select id as id, user_id as userId, op as op, op_time as opTime from sample_oplog where id= #{id}")
    Oplog findById(Integer id);
}
