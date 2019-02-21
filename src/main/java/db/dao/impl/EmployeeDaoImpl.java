package db.dao.impl;

import db.dao.EmployeeDao;
import db.dao.util.ConnectionManager;
import db.dao.util.DatabaseRequests;
import db.entity.Department;
import db.entity.Employee;
import db.entity.Post;
import db.entity.Sex;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDaoImpl implements EmployeeDao {

    private static volatile EmployeeDaoImpl instance;
    private static Connection connection = ConnectionManager.getConnection();

    private EmployeeDaoImpl() {

    }

    public static EmployeeDaoImpl getInstance() {
        EmployeeDaoImpl localInstance = instance;
        if (localInstance == null) {
            synchronized (EmployeeDaoImpl.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new EmployeeDaoImpl();
                }
            }
        }
        return localInstance;
    }

    public static void main(String[] args) {
        EmployeeDaoImpl employeeDao = EmployeeDaoImpl.getInstance();
        Connection connection = ConnectionManager.getConnection();
//        System.out.println(employeeDao.findAllEmployees());
//        Employee employee = new Employee();
//        employee.setSex(Sex.WOMAN);
//        employee.setTelephone(233232L);
//        Department d = new Department();
//        d.setDepartmentCode(10);
//        Post post = new Post();
//        post.setJobCode(4);
//        employee.setDepartment(d);
//        employee.setPost(post);
//        employeeDao.createEmployee(employee);
////        employeeDao.deleteEmployee(1l);
//        employeeDao.updateEmployee("Martin",30,"London", 347537453847l, "lol@gmail.com", 2l);
        System.out.println(employeeDao.findEmployeesByDepartment(10));
        ConnectionManager.closeConnection(connection);
    }

    public List<Employee> findAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DatabaseRequests.SELECT_ALL_EMPLOYEES)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Employee employee = new Employee();
                Post post = new Post();
                Department department = new Department();
                employee.setPost(post);
                employee.setDepartment(department);
                employee.setId(resultSet.getLong(1));
                employee.setName(resultSet.getString(2));
                employee.setLastName(resultSet.getString(3));
                employee.setExperience(resultSet.getInt(4));
                employee.setSex(Sex.valueOf(resultSet.getString(5).toUpperCase()));
                employee.setDateOfBirthday(resultSet.getDate(6));
                employee.setAddress(resultSet.getString(7));
                employee.setEmail(resultSet.getString(8));

                post.setPostName(resultSet.getString(9));

                department.setDepartmentName(resultSet.getString(10));
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public List<Employee> findEmployeesByDepartment(int departmentCode) {
        List<Employee> employeeList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DatabaseRequests.SELECT_EMPLOYEE_BY_DEPARTMENT)) {
            preparedStatement.setInt(1, departmentCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Employee employee = new Employee();
                Post post = new Post();
                Department department = new Department();
                employee.setPost(post);
                employee.setDepartment(department);
                employee.setId(resultSet.getLong(1));
                employee.setName(resultSet.getString(2));
                employee.setLastName(resultSet.getString(3));
                employee.setExperience(resultSet.getInt(4));
                employee.setSex(Sex.valueOf(resultSet.getString(5).toUpperCase()));
                employee.setDateOfBirthday(resultSet.getDate(6));
                employee.setAddress(resultSet.getString(7));
                employee.setEmail(resultSet.getString(8));

                post.setPostName(resultSet.getString(9));

                department.setDepartmentName(resultSet.getString(10));

                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public void deleteEmployee(Long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DatabaseRequests.DELETE_EMPLOYEE)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public void updateEmployee(String lastName, int exp, String add, Long tel, String email,Long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DatabaseRequests.UPDATE_EMPLOYEE)) {
            preparedStatement.setString(1, lastName);
            preparedStatement.setInt(2, exp);
            preparedStatement.setString(3, add);
            preparedStatement.setLong(4, tel);
            preparedStatement.setString(5, email);
            preparedStatement.setLong(6, id);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public Long createEmployee(Employee employee) {
        Long id = null;

        ResultSet generatedKeys = null;

        if (employee != null) {
            try (PreparedStatement ps = connection.prepareStatement(DatabaseRequests.INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, employee.getName());
                ps.setString(2, employee.getLastName());
                ps.setString(3, employee.getThirdName());
                ps.setInt(4, employee.getExperience());
                ps.setString(5, employee.getSex().toString());
                ps.setDate(6, (Date) employee.getDateOfBirthday());
                ps.setString(7, employee.getAddress());
                ps.setLong(8, employee.getTelephone());
                ps.setString(9, employee.getEmail());
                ps.setLong(10, employee.getDepartment().getDepartmentCode());
                ps.setLong(11, employee.getPost().getJobCode());
                ps.executeUpdate();

                generatedKeys = ps.getGeneratedKeys();

                if (null != generatedKeys && generatedKeys.next()) {
                    id = generatedKeys.getLong(1);
                }

            } catch (SQLException ex) {
                ex.getMessage();
            }
        }
        return id;
    }
}
