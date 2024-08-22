package datn.be.mycode.service.impl;

import datn.be.mycode.entity.TheLoai;
import datn.be.mycode.repository.TheLoaiRepository;
import datn.be.mycode.service.TheLoaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TheLoaiServiceImpl implements TheLoaiService {

    @Autowired
    private TheLoaiRepository repo;

    @Override
    public List<TheLoai> getAll() {
        return repo.findAll();
    }

    @Override
    public Optional<TheLoai> getById(Long id) {
        return repo.findById(id);
    }

    @Override
    public void add(TheLoai theLoai) {
        repo.save(theLoai);
    }

    @Override
    public void update(TheLoai theLoai) {
        repo.save(theLoai);
    }

    @Override
    public void delete(TheLoai theLoai) {
        repo.delete(theLoai);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
