package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll(){
		return dao.findAll();  //acessa o BD
	}
	
/*	private DepartmentService service;  //Declarar uma dependêcia dessa classe com o controlador departmentList

	//Lá na classe DepartmentList: setDepartmentService: Injetar a independência na classe sem ser forte, sem colocar a implementação (new Department)
	
	public List<Department> findAll(){
		List<Department> list = new ArrayList<>(); //MOCK: retornar dados de mentirinha só p teste
		list.add(new Department(1, "Books"));
		list.add(new Department(2, "Computers"));
		list.add(new Department(3, "Eletronics"));
		return list;
	}
*/	
	public void saveOrUpdate(Department obj) {  //salva ou atualiza o departamento
		if (obj.getId() == null) {  //se id é nulo, é um novo department, então salva
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Department obj) {  //remove departament do BD
		dao.deleteById(obj.getId());
	}
}