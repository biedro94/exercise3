package wdsr.exercise3.client;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import wdsr.exercise3.model.Product;
import wdsr.exercise3.model.ProductType;

public class ProductService extends RestClientBase {
	protected ProductService(final String serverHost, final int serverPort, final Client client) {
		super(serverHost, serverPort, client);
	}
	
	/**
	 * Looks up all products of given types known to the server.
	 * @param types Set of types to be looked up
	 * @return A list of found products - possibly empty, never null.
	 */
	public List<Product> retrieveProducts(Set<ProductType> types) {
		
		List<Product> productList = new ArrayList<Product>();		
		Response response = baseTarget.path("/products").request(MediaType.APPLICATION_JSON).get(Response.class);		
		List<Product> allProducts = response.readEntity(new GenericType<List<Product>>(){});
		
		for(Product product : allProducts){
			for(ProductType curret :types){
				if(curret.equals(product.getType())){
					productList.add(product);
				}
			}
		}		
		
		return productList;
	}
	
	/**
	 * Looks up all products known to the server.
	 * @return A list of all products - possibly empty, never null.
	 */
	public List<Product> retrieveAllProducts() {
		
		List<Product> productList = new ArrayList<Product>();		
		Response response = baseTarget.path("/products").request(MediaType.APPLICATION_JSON).get(Response.class);		
		List<Product> allProducts = response.readEntity(new GenericType<List<Product>>(){});
		
		for(Product product : allProducts){
			productList.add(product);
		}		
		
		return productList;
	}
	
	/**
	 * Looks up the product for given ID on the server.
	 * @param id Product ID assigned by the server
	 * @return Product if found
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public Product retrieveProduct(int id) {
		
		Response response = baseTarget.path("/products/"+id).request(MediaType.APPLICATION_JSON).get(Response.class);	
		
		try{
			Product productById = response.readEntity(new GenericType<Product>(){});
			return productById;
		}catch(Exception e){
			throw new NotFoundException();
		}
	}	
	
	/**
	 * Creates a new product on the server.
	 * @param product Product to be created. Must have null ID field.
	 * @return ID of the new product.
	 * @throws WebApplicationException if request to the server failed
	 */
	public int storeNewProduct(Product product) {


		Response response = baseTarget.path("/products").request(MediaType.APPLICATION_JSON).post(Entity.entity(product, MediaType.APPLICATION_JSON));
				
		if(response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()){
			throw new WebApplicationException();
		}
				
		return 0;
	}
	
	/**
	 * Updates the given product.
	 * @param product Product with updated values. Its ID must identify an existing resource.
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public void updateProduct(Product product) {		
		
		Response response = baseTarget.path("/products/"+product.getId()).request(MediaType.APPLICATION_JSON).put(Entity.entity(product, MediaType.APPLICATION_JSON));
	
		if(response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()){
			throw new NotFoundException();
		}
	}

	
	/**
	 * Deletes the given product.
	 * @param product Product to be deleted. Its ID must identify an existing resource.
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public void deleteProduct(Product product) {
		Response response = baseTarget.path("/products/"+product.getId()).request(MediaType.APPLICATION_JSON).delete();
		
		if(response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()){
			throw new NotFoundException();
		}
	}
}
