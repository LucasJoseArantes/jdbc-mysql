package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Product;
import entities.Order;
import entities.OrderStatus;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DB.getConnection();
	
		Statement st = conn.createStatement();
			
		ResultSet rs = st.executeQuery("select * FROM tb_order "
					+ "INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id "
					+ "INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");			

		
		Map<Long, Order> map = new HashMap<>();
		Map<Long, Product> prods = new HashMap<>();
		// Utilizamos o Map para depois impedir que tenhamos pedidos e produtos repetidos

		// comando next() testa a próxima linha, e retorna um boolean se a linha existe ou não
		while (rs.next()) {

			Long orderId = rs.getLong("order_id");

			if(map.get(orderId) == null){
				Order order = instantiateOrder(rs);
				map.put(orderId, order);

			}

			Long productId = rs.getLong("product_id");

			if(prods.get(productId) == null){
				Product p = instantiateProduct(rs);
				prods.put(productId, p);
			}

			map.get(orderId).getProducts().add(prods.get(productId));
			// asssociamos o nosso produto com a lista de produtos da classe de pedido;
		}

		for (Long orderId : map.keySet()){
			System.out.println(map.get(orderId));
				for(Product p : map.get(orderId).getProducts()){
					System.out.println(p);
				}
				System.out.println();
		}
	}

	// objeto rs recebe o valor do banco de dados conforme o nome da coluna
	private static Order instantiateOrder (ResultSet rs) throws SQLException{
		Order order = new Order();
		order.setId(rs.getLong("order_id"));
		order.setLatitude(rs.getDouble("latitude"));
		order.setLongitude(rs.getDouble("longitude"));
		order.setMoment(rs.getTimestamp("moment").toInstant());
		order.setStatus(OrderStatus.values()[rs.getInt("status")]);

		return order;

	}

	private static Product instantiateProduct(ResultSet rs) throws SQLException{
		Product p = new Product();

		p.setId(rs.getLong("product_id"));
		p.setName(rs.getString("name"));
		p.setPrice(rs.getDouble("price"));
		p.setDesc(rs.getString("description"));
		p.setImageUri(rs.getString("image_uri"));

		return p;
	}
}
