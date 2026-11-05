==================================================
RECETAS DELUXE - DOCUMENTACION DEL PROYECTO
==================================================

Recetas Deluxe es una aplicacion nativa para Android desarrollada en Kotlin que permite explorar un catalogo de recetas internacionales desde una API REST, guardar tus recetas favoritas de forma local, crear y gestionar tus propias recetas personalizadas con fotos de la galeria y traducirlas automaticamente al idioma del dispositivo.

--------------------------------------------------
1. ARQUITECTURA Y NAVEGACION
--------------------------------------------------

- Arquitectura de Unica Actividad (Single-Activity):
  - MainActivity.kt: Contenedor principal que administra el ViewPager2 y la barra de navegacion inferior (BottomNavigationView).
  - Navegacion estilo Instagram: Permite cambiar de pantalla tanto tocando los iconos como deslizando lateralmente con el dedo (Swipe).
  - Control de Boton Atras: Presionar "Atras" en cualquier pestaña secundaria regresa siempre a la pantalla principal de Inicio antes de salir de la aplicacion.

- Pestañas Principales (Fragments):
  - HomeFragment.kt: Muestra el catalogo de recetas descargado de la API DummyJSON. Incluye buscador (SearchView) en tiempo real y menu de filtros dinamicos por Dificultad, Porciones, Cocina y Etiquetas.
  - FavoriteFragment.kt: Muestra unicamente las recetas marcadas como favoritas por el usuario. Si no hay favoritos, muestra una pantalla vacia informativa.
  - OriginalRecipesFragment.kt: Permite crear, ver (modo solo lectura), editar y eliminar recetas propias personalizadas guardadas en SQLite local, permitiendo adjuntar fotos desde la galeria mediante GetContent() y permisos de lectura persistente.

- Pantalla de Detalle:
  - DetailActivity.kt: Muestra la informacion completa de una receta seleccionada (imagen, titulo, valoracion con estrellas, contador de opiniones, chips de etiquetas/cocina/tipo de comida, metricas de preparacion, cocinado, calorias y porciones, ingredientes, instrucciones y botones de busqueda en YouTube y Google).

--------------------------------------------------
2. BASE DE DATOS LOCAL (SQLITE)
--------------------------------------------------

- DatabaseManager.kt (utils):
  - Hereda de SQLiteOpenHelper para crear y mantener la base de datos local RecetasDeluxe.db (Version 2).
  - Tabla my_recipes: Almacena las recetas propias del usuario (id, nombre, ingredientes, instrucciones e image_uri).
  - Tabla favorite_recipes: Almacena de forma ligera los IDs numéricos de las recetas de la API marcadas como favoritas.

- RecipeDAO.kt (data):
  - Objeto de acceso a datos con metodos CRUD completos (insertar, actualizar, eliminar, consultar por ID y listar) para recetas propias y favoritos.

--------------------------------------------------
3. TRADUCCION AUTOMATICA (GOOGLE ML KIT)
--------------------------------------------------

- RecipeTranslator.kt (utils):
  - Detecta automaticamente el idioma del dispositivo mediante Locale.getDefault().language.
  - Utiliza Google ML Kit Translation para descargar e inicializar el modelo de traduccion en local/offline.
  - Traduce dinamicamente nombres de recetas, tipos de cocina, dificultades, etiquetas, ingredientes e instrucciones al idioma del telefono del usuario.

--------------------------------------------------
4. ESTRUCTURA DE ARCHIVOS Y CARPETAS
--------------------------------------------------

- java/com/nicolas/recetasdeluxe/
  - activities/
    - MainActivity.kt
    - DetailActivity.kt
  - fragments/
    - HomeFragment.kt
    - FavoriteFragment.kt
    - OriginalRecipesFragment.kt
  - adapters/
    - MainPagerAdapter.kt
    - RecipeAdapter.kt
    - OriginalRecipeAdapter.kt
  - data/
    - Recipe.kt
    - MyRecipe.kt
    - RecipesAPI.kt
    - RecipeDAO.kt
  - utils/
    - DatabaseManager.kt
    - RecipeDiffUtil.kt
    - RecipeTranslator.kt

- res/
  - layout/
    - activity_main.xml
    - activity_detail.xml
    - activity_favorite.xml
    - activity_original_recipes.xml
    - fragment_home.xml
    - item_recipe.xml
    - original_recipes.xml
    - dialog_add_recipe.xml (Con ScrollView y selector de imagen)
  - values/ y values-es/
    - strings.xml (Recursos de texto traducidos al ingles y español)
    - themes.xml (Tema Material3 NoActionBar)
