Use Case:

1.	As a customer, I want to view products in a category and have them arranged on the page based on inventory availability or price that I select.
2.	As an admin, I want to be able to add, and remove product or update product prices and/or inventory at any time.


Business Criteria:

1.	The category page should not display products if their inventory is unavailable or limited.
2.	If there are no products available with sufficient inventory, an error message should be displayed on the category page.
3.	If a non-existent category is requested, an error message is to be sent back



API Gateway Port: 9191
Admin-Service - 8083
Customer-Service - 8084

Admin can access following 

GET:
  http://localhost:9191/api/products/admin
  http://localhost:9191/api/products/admin/inventory-limited-or-unavailable
  http://localhost:9191/api/products/admin/53
  http://localhost:9191/api/products/admin/category/shirt
  http://localhost:9191/api/products/admin/category/shirt/sort/INVENTORY
  http://localhost:9191/api/products/admin/category/shirt/sort/PRICE
  
POST:
  http://localhost:9191/api/products/admin

PUT:
  http://localhost:9191/api/products/admin/52

DELETE:
  http://localhost:9191/api/products/admin/52

Customer-Service:
  GET:
    http://localhost:9191/api/products/customer
    http://localhost:9191/api/products/customer/55
    http://localhost:9191/api/products/customer/category/shirt
    http://localhost:9191/api/products/customer/category/shirt/sort/INVENTORY
    http://localhost:9191/api/products/customer/category/shirt/sort/PRICE
  


  
