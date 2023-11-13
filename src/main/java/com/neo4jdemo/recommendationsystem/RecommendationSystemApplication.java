package com.neo4jdemo.recommendationsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class RecommendationSystemApplication implements CommandLineRunner {

	@Autowired
	public PersonRepository personRepository;

	@Autowired
	public PlaceRepository placeRepository;

	public final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		SpringApplication.run(RecommendationSystemApplication.class, args);
	}

	public void run(String... args) {
		System.out.println("APLICACAO INICIALIZADA");
//		clearDB();
//		populateDB();
		System.out.println("Bem-vindo ao sistema de recomendações!");
		l1:while (true) {
			int option = userMenu("Digite a opção desejada", Map.of(
					0, "Sair.",
					1, "Consultas.",
					2, "Adicionar pessoa.",
					3, "Adicionar lugar.",
					4, "Adicionar amizade.",
					5, "Adicionar curtida.",
					6, "Deletar pessoa.",
					7, "Deletar lugar.",
					8, "Remover amizade.",
					9, "Remover curtida."
			));
			switch (option) {
				case 0 -> {
					break l1;
				}
				case 1 -> queries();
				case 2 -> addPerson();
				case 3 -> addPlace();
				case 4 -> addFriendship();
				case 5 -> addLike();
				case 6 -> deletePerson();
				case 7 -> deletePlace();
				case 8 -> removeFriendship();
				case 9 -> removeLike();
			}
		}
		System.out.println("FINALIZANDO APLICACAO");
	}

	public void queries() {
		l1:while (true) {
			int option = userMenu("Digite a opção desejada", Map.of(
					0, "Voltar.",
					1, "Ver todas as pessoas.",
					2, "Ver todos os lugares.",
					3, "Ver recomendações por amizades.",
					4, "Ver recomendações por idade dos frequentadores.",
					5, "Ver recomendações por tipo e amizades.",
					6, "Ver número de amizades por pessoa.",
					7, "Ver popularidade de lugares.",
					8, "Ver pessoas por interesses similares."
			));
			switch (option) {
				case 0 -> {
					break l1;
				}
				case 1 -> this.personRepository.findAll().forEach(System.out::println);
				case 2 -> this.placeRepository.findAll().forEach(System.out::println);
				case 3 -> recommendationsByFriendship();
				case 4 -> recommendationsByAge();
				case 5 -> recommendationsByFriendshipAndType();
				case 6 -> this.personRepository.findFriendshipCount().forEach(System.out::println);
				case 7 -> this.placeRepository.findLikeCount().forEach(System.out::println);
				case 8 -> personWithPlacesInCommon();
			}
		}
	}

	public void addPerson() {
		System.out.println("Insira as informações da pessoa:");
		String name = genericInput("Nome:");
		Integer age = integerInput("Age:");
		Person person = Person.builder().name(name).age(age).build();
		this.personRepository.save(person);
	}

	public void addPlace() {
		System.out.println("Insira as informações do lugar:");
		String name = genericInput("Nome:");
		String type = genericInput("Tipo:");
		Place place = Place.builder().name(name).type(type).build();
		this.placeRepository.save(place);
	}

	public void addFriendship() {
		List<Person> personList = this.personRepository.findAll();
		int index = userMenu("Escolha uma pessoa:", personList.stream().map(Person::getName).toList());
		Person person1 = personList.remove(index);
		index = userMenu("Escolha outra pessoa:", personList.stream().map(Person::getName).toList());
		Person person2 = personList.get(index);
		person1.addFriend(person2);
		this.personRepository.save(person1);
	}

	public void addLike() {
		List<Person> personList = this.personRepository.findAll();
		int index = userMenu("Escolha uma pessoa:", personList.stream().map(Person::getName).toList());
		Person person = personList.get(index);
		List<Place> placeList = this.placeRepository.findAll();
		index = userMenu("Escolha um lugar:", placeList.stream().map(Place::getName).toList());
		person.addLikedPlace(placeList.get(index));
		this.personRepository.save(person);
	}

	public void deletePerson() {
		List<Person> personList = this.personRepository.findAll();
		int index = userMenu("Escolha uma pessoa para deletar:", personList.stream().map(Person::getName).toList());
		this.personRepository.delete(personList.get(index));
	}

	public void deletePlace() {
		List<Place> placeList = this.placeRepository.findAll();
		int index = userMenu("Escolha um lugar para deletar:", placeList.stream().map(Place::getName).toList());
		this.placeRepository.delete(placeList.get(index));
	}

	public void removeFriendship() {
		List<Person> personList = this.personRepository.findAll();
		int index = userMenu("Escolha uma pessoa para remover amizade:", personList.stream().map(Person::getName).toList());
		Person person1 = personList.get(index);
		List<Person> friends = this.personRepository.findAllFriends(person1.getName());
		index = userMenu("Escolha outra pessoa:", friends.stream().map(Person::getName).toList());
		Person person2 = friends.get(index);

		// Custom query allows us to ignore the direction of the relationship
		this.personRepository.removeFriendship(person1.getName(), person2.getName());
	}

	public void removeLike() {
		List<Person> personList = this.personRepository.findAll();
		int index = userMenu("Escolha uma pessoa para remover curtida:", personList.stream().map(Person::getName).toList());
		Person person = personList.get(index);
		index = userMenu("Escolha um lugar para descurtir:", person.getLikedPlaces().stream().map(Place::getName).toList());
		Place place = person.getLikedPlaces().get(index);
		this.personRepository.removeLike(person.getName(), place.getName());
	}

	public void recommendationsByFriendship() {
		List<Person> personList = this.personRepository.findAll();
		int index = userMenu("Escolha para quem será a recomendação:", personList.stream().map(Person::getName).toList());
		this.placeRepository.findPlacesByFriendshipDepth1(personList.get(index).getName())
				.forEach(System.out::println);
		String expand = genericInput("Deseja obter mais resultados? (S) para Sim, outra tecla para Não.");
		if (expand.equals("S")) {
			this.placeRepository.findPlacesByFriendshipDepth2(personList.get(index).getName())
					.forEach(System.out::println);
			expand = genericInput("Deseja obter mais resultados? (S) para Sim, outra tecla para Não.");
			if (expand.equals("S")) {
				this.placeRepository.findPlacesByFriendshipDepth3(personList.get(index).getName())
						.forEach(System.out::println);
			}
		}
	}

	public void recommendationsByAge() {
		int min = integerInput("Idade mínima:");
		int max = integerInput("Idade máxima:");
		this.placeRepository.findPlacesByUserAge(min, max).forEach(System.out::println);
	}

	public void recommendationsByFriendshipAndType() {
		List<Person> personList = this.personRepository.findAll();
		int index = userMenu("Escolha para quem será a recomendação:", personList.stream().map(Person::getName).toList());
		List<String> placeTypes = this.placeRepository.findAllTypes();
		int typeIndex = userMenu("Escolha o tipo de lugar:", placeTypes);
		this.placeRepository.findPlacesByTypeAndFriendshipD1(personList.get(index).getName(), placeTypes.get(typeIndex))
				.forEach(System.out::println);
		String expand = genericInput("Deseja obter mais resultados? (S) para Sim, outra tecla para Não.");
		if (expand.equals("S")) {
			this.placeRepository.findPlacesByTypeAndFriendshipD2(personList.get(index).getName(), placeTypes.get(typeIndex))
					.forEach(System.out::println);
			expand = genericInput("Deseja obter mais resultados? (S) para Sim, outra tecla para Não.");
			if (expand.equals("S")) {
				this.placeRepository.findPlacesByTypeAndFriendshipD3(personList.get(index).getName(), placeTypes.get(typeIndex))
						.forEach(System.out::println);
			}
		}
	}

	public void personWithPlacesInCommon() {
		List<Person> personList = this.personRepository.findAll();
		int index = userMenu("Escolha a pessoa inicial:", personList.stream().map(Person::getName).toList());
		this.personRepository.findPersonWithPlacesInCommon(personList.get(index).getName()).forEach(System.out::println);
	}

	public void populateDB() {
		List<String> names = Arrays.asList("Ana", "Bruno", "Carla", "Daniel", "Elisa", "Felipe", "Gabriela", "Henrique", "Isabela", "João", "Lucia", "Marcos");
		Map<String, List<String>> placeNameByType = new HashMap<>();
		placeNameByType.put("restaurant", Arrays.asList("Cavanhas", "Outback", "Ciao", "Mamma Mia", "Japesca", "Burger King", "McDonald's"));
		placeNameByType.put("park", Arrays.asList("Redenção", "Parcao", "Marinha", "Orla"));
		placeNameByType.put("mall", Arrays.asList("Iguatemi", "Praia de Belas", "Bourbon Wallig", "Barra Shopping Sul, Shopping Total"));
		placeNameByType.put("museum", Arrays.asList("MARGS", "Santander Cultural", "Casa de Cultura Mário Quintana", "Fundação Iberê Camargo", "Museu da PUCRS"));
		placeNameByType.put("market", Arrays.asList("Zaffari", "Mercado Público", "BIG", "Carrefour"));

		List<Person> personList = names
				.stream()
				.map(name -> Person.builder().name(name)
					.age(ThreadLocalRandom.current().nextInt(20, 41)).build())
				.toList();
		List<Place> placeList = placeNameByType.entrySet()
				.stream()
				.flatMap(entry -> entry.getValue().stream().map(value -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), value)))
				.map(entry -> Place.builder().type(entry.getKey()).name(entry.getValue()).build())
				.toList();

		personList.get(0).setFriends(List.of(personList.get(1), personList.get(2)));
		personList.get(1).setFriends(List.of(personList.get(3), personList.get(4)));
		personList.get(2).setFriends(List.of(personList.get(5), personList.get(6)));
		personList.get(3).setFriends(List.of(personList.get(7), personList.get(8)));
		personList.get(4).setFriends(List.of(personList.get(9), personList.get(10)));
		personList.get(5).setFriends(List.of(personList.get(11), personList.get(0)));

		personList.forEach(p ->
				p.setLikedPlaces(List.of(
						placeList.get(ThreadLocalRandom.current().nextInt(0, placeList.size())),
						placeList.get(ThreadLocalRandom.current().nextInt(0, placeList.size())),
						placeList.get(ThreadLocalRandom.current().nextInt(0, placeList.size()))
				))
		);

		personRepository.saveAll(personList);
		placeRepository.saveAll(placeList);
	}

	public void clearDB() {
		personRepository.deleteAll();
		placeRepository.deleteAll();
	}

	public int userMenu(String mensagemInicial, Map<Integer, String> options) {
		List<Integer> numbers = new ArrayList<>(options.keySet());
		numbers.sort(Comparator.naturalOrder());
		System.out.println(mensagemInicial);
		numbers.forEach(number -> System.out.println("(" + number + ") " + options.get(number)));
		while (true) {
			try {
				int option = Integer.parseInt(scanner.nextLine());
				if (options.containsKey(option)) {
					return option;
				} else {
					System.out.println("Opção inválida! Digite novamente:");
				}
			} catch (Exception e) {
				System.out.println("Opção inválida! Digite novamente:");
			}
		}
	}

	public int userMenu(String mensagemInicial, List<String> options) {
		if (mensagemInicial != null) {
			System.out.println(mensagemInicial);
		}
		for (int i = 1; i <= options.size(); i++) {
			System.out.println("(" + i + ") " + options.get(i - 1));
		}
		while (true) {
			try {
				int option = Integer.parseInt(scanner.nextLine());
				if (option > 0 && option <= options.size()) {
					return option - 1;
				} else {
					System.out.println("Opção inválida! Digite novamente:");
				}
			} catch (Exception e) {
				System.out.println("Opção inválida! Digite novamente:");
			}
		}
	}

	public String genericInput(String message) {
		System.out.println(message);
		return scanner.nextLine();
	}

	public Integer integerInput(String message) {
		System.out.println(message);
		while (true) {
			try {
				return Integer.parseInt(scanner.nextLine());
			} catch (Exception e) {
				System.out.println("Valor inválido! Digite novamente:");
			}
		}
	}
}
