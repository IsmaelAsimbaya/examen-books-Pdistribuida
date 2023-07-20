package com.distribuida.app.books.rest;

import com.distribuida.app.books.clients.AuthorsRestClient;
import com.distribuida.app.books.db.Book;
import com.distribuida.app.books.dtos.AuthorDto;
import com.distribuida.app.books.dtos.BookDto;
import com.distribuida.app.books.repo.BookRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class BookRest {

    @Inject
    BookRepository rep;

    @Inject
    @RestClient
    AuthorsRestClient clientAuthors;

    static BookDto fromBook(Book obj){
        BookDto dto = new BookDto();

        dto.setId(obj.getId());
        dto.setIsbn(obj.getIsbn());
        dto.setTitle(obj.getTitle());
        dto.setPrice(obj.getPrice());
        dto.setAuthor_id(obj.getAuthor_id());

        return dto;
    }

    @GET
    public List<BookDto> findAll() {

        return rep.streamAll()
                .map(BookRest::fromBook)
                .map(dto->{
                    AuthorDto authorDto = clientAuthors.getById(dto.getAuthor_id());
                    String aname = String.format("%s, %s", authorDto.getLastName()
                            ,authorDto.getFirstName());
                    System.out.println("*******"+ aname);
                    dto.setAuthorName(aname);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        var book = rep.findByIdOptional(id);

        //System.out.println("*******" + clientAuthors);

        if (book.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Book obj = book.get();

        BookDto dto = new BookDto();

        AuthorDto authorDto = clientAuthors.getById(obj.getAuthor_id());

        dto.setId(obj.getId());
        dto.setIsbn(obj.getIsbn());
        dto.setTitle(obj.getTitle());
        dto.setPrice(obj.getPrice());
        dto.setAuthor_id(obj.getAuthor_id());
        System.out.println(authorDto);
        String aname = String.format("%s, %s", authorDto.getLastName(),authorDto.getFirstName());
        dto.setAuthorName(aname);


        return Response.ok(dto).build();
    }

    @POST
    public Response create(Book p) {
        rep.persist(p);

        return Response.status(Response.Status.CREATED.getStatusCode(), "book created").build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Book bookObj) {
        Book book = rep.findById(id);

        book.setIsbn(bookObj.getIsbn());
        book.setPrice(bookObj.getPrice());
        book.setTitle(bookObj.getTitle());

        //rep.persistAndFlush(book);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        rep.deleteById(id);

        return Response.ok( )
                .build();
    }


}
