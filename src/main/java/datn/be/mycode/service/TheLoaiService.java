package datn.be.mycode.service;

import datn.be.mycode.entity.TheLoai;

import java.util.List;
import java.util.Optional;

public interface TheLoaiService {

    List<TheLoai> getAll();

    Optional<TheLoai> getById(Long id);

    void add(TheLoai theLoai);

    void update(TheLoai theLoai);

    void delete  (TheLoai theLoai);

    void deleteById  (Long id);
}
