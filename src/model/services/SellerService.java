package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {

	private SellerDao dao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll(){
		return dao.findAll();  //acessa o BD
	}
	
	public void saveOrUpdate(Seller obj) {  //salva ou atualiza 
		if (obj.getId() == null) {  //se id é nulo, é um novo vendedor, então salva
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Seller obj) {  //remove seller do BD
		dao.deleteById(obj.getId());
	}
}