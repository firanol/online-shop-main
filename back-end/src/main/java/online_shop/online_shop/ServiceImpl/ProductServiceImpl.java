package online_shop.online_shop.ServiceImpl;

import online_shop.online_shop.adapter.ProductAdapter;
import online_shop.online_shop.domain.Category;
import online_shop.online_shop.domain.Product;
import online_shop.online_shop.dto.ProductRequestDto;
import online_shop.online_shop.dto.response.FileUploadResponse;
import online_shop.online_shop.dto.response.ProductResponseDto;
import online_shop.online_shop.repository.ProductRepository;
import online_shop.online_shop.service.ProductService;
import online_shop.online_shop.util.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto productDto) {
        final FileUploadResponse response = saveImageUrl(productDto.image());
        Product product = ProductAdapter.getProductFromProductRequsetDto(productDto, response.getFileDownloadUri());
        productRepository.save(product);
        return ProductAdapter.getProductDtoFromProduct(product);
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductAdapter.getProductDtoFromProduct(product);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return ProductAdapter.getProductDtoListFromProductList(productRepository.findAll());
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto productDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        if (productDto.image() != null) {
            final FileUploadResponse response = saveImageUrl(productDto.image());
            if (response != null) {
                product.setImageUrl(response.getFileDownloadUri());
            }
        }
        product.setName(productDto.name());
        product.setDescription(productDto.description());
        product.setPrice(productDto.price());
        product.setCategory(new Category(Long.parseLong(productDto.categoryId())));
        Product updatedProduct = productRepository.save(product);
        return ProductAdapter.getProductDtoFromProduct(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponseDto updateProductImage(Long id, MultipartFile image) {
        final FileUploadResponse response = saveImageUrl(image);
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setImageUrl(response.getFileDownloadUri());
        productRepository.save(product);
        return ProductAdapter.getProductDtoFromProduct(product);
    }

    private FileUploadResponse saveImageUrl(MultipartFile file) {
        if (file == null) {
            return null;
        }
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/products/downloadFile/")
                .path(fileName)
                .toUriString();
        return new FileUploadResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }
}
