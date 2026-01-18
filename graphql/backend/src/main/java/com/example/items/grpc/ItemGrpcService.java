package com.example.items.grpc;

import com.example.items.repo.ItemRepository;
import com.example.items.service.CsvService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class ItemGrpcService extends ItemServiceGrpc.ItemServiceImplBase {

    private final CsvService csvService;
    private final ItemRepository repository;

    public ItemGrpcService(CsvService csvService, ItemRepository repository) {
        this.csvService = csvService;
        this.repository = repository;
    }

    @Override
    public void uploadCsv(
            UploadCsvRequest request,
            StreamObserver<UploadCsvResponse> responseObserver) {

        var items = csvService.parse(request.getFile().toByteArray());
        repository.saveAll(items);

        responseObserver.onNext(
            UploadCsvResponse.newBuilder().setSuccess(true).build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getItems(
            GetItemsRequest request,
            StreamObserver<GetItemsResponse> responseObserver) {

        var items = repository.findAll().stream()
            .map(i -> Item.newBuilder()
                .setId(i.getId())
                .setName(i.getName())
                .setAge(i.getAge())
                .setCity(i.getCity())
                .build())
            .toList();

        responseObserver.onNext(
            GetItemsResponse.newBuilder().addAllItems(items).build()
        );
        responseObserver.onCompleted();
    }
}