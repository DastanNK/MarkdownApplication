package com.dastan.markdownapplication.di

import android.content.Context
import androidx.room.Room
import com.dastan.markdownapplication.data.local.AppDatabase
import com.dastan.markdownapplication.data.local.FileDao
import com.dastan.markdownapplication.data.repositiry.CachedFileRepository
import com.dastan.markdownapplication.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext ctx: Context
    ): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "markdown.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideFileDao(db: AppDatabase): FileDao = db.fileDao()

    @Provides
    @Singleton
    fun provideCachedFileRepository(
        dao: FileDao
    ): CachedFileRepository = CachedFileRepository(dao)

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideAddFileUseCase(repo: CachedFileRepository) =
        AddFileUseCase(repo)

    @Provides
    fun provideGetAllFilesUseCase(repo: CachedFileRepository) =
        GetAllFilesUseCase(repo)

    @Provides
    fun provideGetLastOpenedUseCase(repo: CachedFileRepository) =
        GetLastOpenedFileUseCase(repo)

    @Provides
    fun provideMarkFileOpenedUseCase(repo: CachedFileRepository) =
        MarkFileOpenedUseCase(repo)

    @Provides
    fun provideImportFileUseCase(
        addFile: AddFileUseCase,
        repo: CachedFileRepository
    ) = ImportFileUseCase(addFile, repo)
    @Provides
    fun provideImportFromUriUseCase(
        importFile: ImportFileUseCase
    ) = ImportFromUriUseCase(importFile)
    @Provides
    fun provideImportFromUrlUseCase(
        importFile: ImportFileUseCase
    ) = ImportFromUrlUseCase(importFile)
    @Provides
    fun provideParseMarkdownUseCase() = ParseMarkdownUseCase()



}